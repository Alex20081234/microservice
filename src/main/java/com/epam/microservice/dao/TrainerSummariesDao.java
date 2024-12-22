package com.epam.microservice.dao;

import com.epam.microservice.domain.TrainerSummary;

public interface TrainerSummariesDao {
    TrainerSummary getTrainerSummary(String username);

    void updateOrNewDocument(TrainerSummary summary);

    boolean exists(String username);
}
