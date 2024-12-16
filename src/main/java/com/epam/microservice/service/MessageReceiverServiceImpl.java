package com.epam.microservice.service;

import com.epam.microservice.controller.WorkloadController;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import lombok.AllArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageReceiverServiceImpl implements MessageReceiverService {
    private final WorkloadController workloadController;

    @Override
    @JmsListener(destination = "submit-workload-queue")
    public void receiveMessage(SubmitWorkloadChangesRequestBody body) {
        workloadController.submitWorkloadChanges(body);
    }
}
