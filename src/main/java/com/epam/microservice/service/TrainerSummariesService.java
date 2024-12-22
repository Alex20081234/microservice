package com.epam.microservice.service;

import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;

public interface TrainerSummariesService {
    void submitWorkloadChanges(SubmitWorkloadChangesRequestBody body);

    ResponseSummary getSummary(String username);
}
