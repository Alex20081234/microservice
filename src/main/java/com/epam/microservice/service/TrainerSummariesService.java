package com.epam.microservice.service;

import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;

public interface TrainerSummariesService {
    /**
     * Submits workload changes for a trainer.
     *
     * @param body the details of the workload changes to be submitted.
     * @throws IllegalArgumentException if the specified trainer does not exist and the action type is DELETE.
     */
    void submitWorkloadChanges(SubmitWorkloadChangesRequestBody body);

    ResponseSummary getSummary(String username);
}
