package com.epam.microservice.service;

import com.epam.microservice.controller.WorkloadController;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageReceiverServiceImplTest {
    @Mock
    private WorkloadController controller;
    @InjectMocks
    private MessageReceiverServiceImpl receiverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void receiveMessageShouldTryToReceiveMessageAndSendItForProcessing() {
        SubmitWorkloadChangesRequestBody body = SubmitWorkloadChangesRequestBody.builder()
                .trainerUsername("Jane.Doe")
                .trainerFirstName("Jane")
                .trainerLastName("Doe")
                .trainerIsActive(true)
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(30)
                .changeType(ActionType.ADD)
                .build();
        when(controller.submitWorkloadChanges(any())).thenReturn(ResponseEntity.noContent().build());
        receiverService.receiveMessage(body);
        verify(controller, times(1)).submitWorkloadChanges(body);
    }
}
