package com.epam.microservice.dao;

import com.epam.microservice.domain.TrainerSummary;
import java.util.Optional;

public interface TrainerSummariesDao {
    Optional<TrainerSummary> getTrainerSummary(String username);

    void updateOrSave(TrainerSummary summary);

    boolean exists(String username);
}
