package com.epam.microservice.controller;

import com.epam.microservice.common.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private MockMvc mockMvc;

    @Mock
    private WorkloadController controller;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void handleEntityNotFoundExceptionShouldProcessEntityNotFoundException() throws Exception {
        when(controller.getSummary(anyString())).thenThrow(new EntityNotFoundException("No such user"));
        mockMvc.perform(get("/api/v1/workload/summary/Tom.Doe"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No such user"));
    }

    @Test
    void handleIllegalArgumentExceptionShouldProcessIllegalArgumentException() throws Exception {
        when(controller.getSummary(anyString())).thenThrow(new IllegalArgumentException("No such user"));
        mockMvc.perform(get("/api/v1/workload/summary/Tom.Doe"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No such user"));
    }

    @Test
    void handleMethodArgumentNotValidExceptionShouldProcessMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(patch("/api/v1/workload/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"trainerUsername\": \"\", \"trainerFirstName\": \"John\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Validation failed ")));
    }

    @Test
    void handleExceptionShouldProcessException() throws Exception {
        when(controller.getSummary(anyString())).thenThrow(new RuntimeException("Connection lost"));
        mockMvc.perform(get("/api/v1/workload/summary/Tom.Doe"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error occurred : Connection lost"));
    }
}
