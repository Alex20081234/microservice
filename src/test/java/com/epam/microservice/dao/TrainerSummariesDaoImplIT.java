package com.epam.microservice.dao;

import com.epam.microservice.domain.MonthlyWorkload;
import com.epam.microservice.domain.TrainerSummary;
import com.epam.microservice.domain.YearlyWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TrainerSummariesDaoImplIT {
    private MonthlyWorkload monthlyWorkload;
    private YearlyWorkload yearlyWorkload;
    private TrainerSummary summary;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private TrainerSummariesDaoImpl dao;

    @BeforeEach
    void setUp() {
        monthlyWorkload = MonthlyWorkload.builder()
                .month(Month.DECEMBER)
                .workingHours(60)
                .build();
        yearlyWorkload = YearlyWorkload.builder()
                .year(2024)
                .list(new ArrayList<>(List.of(monthlyWorkload)))
                .build();
        summary = TrainerSummary.builder()
                .username("Jane.Doe")
                .firstName("Jane")
                .lastName("Doe")
                .status(true)
                .workloads(new ArrayList<>(List.of(yearlyWorkload)))
                .build();
    }

    void cleanUp() {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(summary.getUsername()));
        template.remove(query, TrainerSummary.class);
    }

    @Test
    void getTrainerSummaryShouldReturnSummary() {
        dao.updateOrSave(summary);
        assertEquals(summary, dao.getTrainerSummary(summary.getUsername()).orElse(null));
        cleanUp();
    }

    @Test
    void existsShouldReturnWhetherDocumentExistsInDB() {
        assertFalse(dao.exists("Nonexistent"));
        dao.updateOrSave(summary);
        assertTrue(dao.exists(summary.getUsername()));
        cleanUp();
    }

    @Test
    void updateOrSaveShouldSaveDocumentWhenNonexistent() {
        dao.updateOrSave(summary);
        assertTrue(dao.exists(summary.getUsername()));
        cleanUp();
    }

    @Test
    void updateOrSaveShouldUpdateDocumentWhenExistsWithSameMonth() {
        dao.updateOrSave(summary);
        dao.updateOrSave(summary);
        summary.getWorkloads().get(0).getList().get(0).setWorkingHours(monthlyWorkload.getWorkingHours());
        assertEquals(summary, dao.getTrainerSummary(summary.getUsername()).orElse(null));
        cleanUp();
    }

    @Test
    void updateOrSaveShouldUpdateDocumentWhenExistsWithDifferentMonth() {
        dao.updateOrSave(summary);
        summary.getWorkloads().get(0).getList().get(0).setMonth(Month.NOVEMBER);
        dao.updateOrSave(summary);
        MonthlyWorkload m1 = MonthlyWorkload.builder()
                .workingHours(60)
                .month(Month.DECEMBER)
                .build();
        MonthlyWorkload m2 = MonthlyWorkload.builder()
                .workingHours(60)
                .month(Month.NOVEMBER)
                .build();
        YearlyWorkload y = YearlyWorkload.builder()
                .year(yearlyWorkload.getYear())
                .list(List.of(m1, m2))
                .build();
        assertEquals(List.of(y), dao.getTrainerSummary(summary.getUsername()).orElse(null).getWorkloads());
        cleanUp();
    }

    @Test
    void updateOrSaveShouldUpdateDocumentWhenExistsWithDifferentYear() {
        dao.updateOrSave(summary);
        summary.getWorkloads().get(0).setYear(2025);
        dao.updateOrSave(summary);
        YearlyWorkload y = YearlyWorkload.builder()
                .year(2024)
                .build();
        y.add(null);
        MonthlyWorkload[] array = new MonthlyWorkload[2];
        array[0] = (MonthlyWorkload) yearlyWorkload.getList().toArray()[0];
        array[1] = null;
        y.add(array);
        assertEquals(List.of(y, yearlyWorkload), dao.getTrainerSummary(summary.getUsername()).orElse(null).getWorkloads());
        cleanUp();
    }
}
