package com.epam.microservice.dao;

import com.epam.microservice.common.Dao;
import com.epam.microservice.common.EntityNotFoundException;
import com.epam.microservice.domain.MonthlyWorkload;
import com.epam.microservice.domain.TrainerSummary;
import com.epam.microservice.domain.YearlyWorkload;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.util.List;

@Dao
@AllArgsConstructor
public class TrainerSummariesDaoImpl implements TrainerSummariesDao {
    private final MongoTemplate template;

    @Override
    public TrainerSummary getTrainerSummary(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        TrainerSummary trainerSummary = template.findOne(query, TrainerSummary.class);
        if (trainerSummary == null) {
            throw new EntityNotFoundException("Trainer summary not found for username: " + username);
        }
        return trainerSummary;
    }

    @Override
    public void updateOrNewDocument(TrainerSummary summary) {
        if (exists(summary.getUsername())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("username").is(summary.getUsername()));
            Update update = new Update();
            update.set("workload", updateWorkload(summary.getUsername(), summary.getWorkload().get(0)));
            template.updateFirst(query, update, TrainerSummary.class);
        } else {
            template.save(summary);
        }
    }

    @Override
    public boolean exists(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return template.exists(query, TrainerSummary.class);
    }

    private List<YearlyWorkload> updateWorkload(String username, YearlyWorkload yearly) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        query.fields().include("workload");
        TrainerSummary summary = template.findOne(query, TrainerSummary.class);
        List<YearlyWorkload> existingWorkload = summary.getWorkload();
        MonthlyWorkload currentMonthWorkload = yearly.getList().get(0);
        boolean changesApplied = false;
        for (YearlyWorkload y : existingWorkload) {
            if (y.getYear() == yearly.getYear()) {
                for (MonthlyWorkload m : y.getList()) {
                    if (m.getMonth() == currentMonthWorkload.getMonth()) {
                        m.setWorkingHours(currentMonthWorkload.getWorkingHours());
                        changesApplied = true;
                    }
                }
                if (!changesApplied) {
                    y.add(currentMonthWorkload);
                    changesApplied = true;
                }
            }
        }
        if (!changesApplied) {
            existingWorkload.add(yearly);
        }
        return existingWorkload;
    }
}
