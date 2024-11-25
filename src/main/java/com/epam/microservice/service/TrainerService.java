package com.epam.microservice.service;

import com.epam.microservice.domain.Trainer;
import com.epam.microservice.domain.Training;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.dto.ResponseSummary;

public interface TrainerService {
    void submitWorkloadChanges(Trainer trainer, Training training, ActionType type);

    ResponseSummary getSummary(String username);
}
