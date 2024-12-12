package com.epam.microservice.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeadLetterQueueSenderServiceImpl implements DeadLetterQueueSenderService {
    private final JmsTemplate jmsTemplate;
    @Qualifier("deadLetterQueueName")
    private final String queueName;

    @Override
    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(queueName, message);
    }
}
