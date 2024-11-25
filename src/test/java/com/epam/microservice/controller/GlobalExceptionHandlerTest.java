package com.epam.microservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {
    private MockMvc mockMvc;

    @Mock
    private WorkloadController controller;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void handleIllegalArgumentExceptionShouldProcessIllegalArgumentException() throws Exception {
        when(controller.getSummary(anyString())).thenThrow(new IllegalArgumentException("No such user"));
        mockMvc.perform(get("/api/v1/workload/summary/Tom.Doe"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No such user"));
    }

    @Test
    void handleExceptionShouldProcessException() throws Exception {
        when(controller.getSummary(anyString())).thenThrow(new RuntimeException("Connection lost"));
        mockMvc.perform(get("/api/v1/workload/summary/Tom.Doe"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error occurred : Connection lost"));
    }
}
