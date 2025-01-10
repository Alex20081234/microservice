package com.epam.microservice.steps.component;

import com.epam.microservice.domain.TrainerSummary;
import io.cucumber.java.After;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@AllArgsConstructor
public class Hooks {
    private MongoTemplate template;

    @After
    public void tearDown() {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is("John.Doe"));
        template.remove(query, TrainerSummary.class);
    }
}
