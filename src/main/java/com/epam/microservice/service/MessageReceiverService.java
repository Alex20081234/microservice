package com.epam.microservice.service;

import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;

public interface MessageReceiverService {

    void receiveMessage(SubmitWorkloadChangesRequestBody body);
}
