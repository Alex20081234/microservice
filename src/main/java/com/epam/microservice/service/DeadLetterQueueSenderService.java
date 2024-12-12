package com.epam.microservice.service;

public interface DeadLetterQueueSenderService {
    void sendMessage(String message);
}
