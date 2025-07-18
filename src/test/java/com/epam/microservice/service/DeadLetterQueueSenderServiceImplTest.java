package com.epam.microservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jms.core.JmsTemplate;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DeadLetterQueueSenderServiceImplTest {
    private static final String QUEUE_NAME = "test-queue";
    private JmsTemplate jmsTemplate;
    private DeadLetterQueueSenderServiceImpl senderService;

    @BeforeEach
    void setUp() {
        jmsTemplate = mock(JmsTemplate.class);
        senderService = new DeadLetterQueueSenderServiceImpl(jmsTemplate, QUEUE_NAME);
    }

    @Test
    void sendMessageShouldTryToSendMessageToDeadLetterQueue() {
        String message = "Test message";
        doNothing().when(jmsTemplate).convertAndSend(anyString(), anyString());
        senderService.sendMessage(message);
        verify(jmsTemplate, times(1)).convertAndSend(QUEUE_NAME, message);
    }
}
