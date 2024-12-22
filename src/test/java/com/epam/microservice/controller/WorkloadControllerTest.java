package com.epam.microservice.controller;

import com.epam.microservice.domain.MonthlyWorkload;
import com.epam.microservice.domain.YearlyWorkload;
import com.epam.microservice.dto.*;
import com.epam.microservice.service.TrainerSummariesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WorkloadControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private MockMvc mockMvc;

    @Mock
    private TrainerSummariesService service;

    @InjectMocks
    private WorkloadController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void submitWorkloadChangesShouldTryToProcessChanges() throws Exception {
        SubmitWorkloadChangesRequestBody body = SubmitWorkloadChangesRequestBody.builder()
                .trainerUsername("Jane.Doe")
                .trainerFirstName("Jane")
                .trainerLastName("Doe")
                .trainerIsActive(true)
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(30)
                .changeType(ActionType.ADD)
                .build();
        doNothing().when(service).submitWorkloadChanges(any());
        mockMvc.perform(patch("/api/v1/workload/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSummaryShouldReturnSummary() throws Exception {
        MonthlyWorkload monthly = MonthlyWorkload.builder()
                .month(Month.OCTOBER)
                .workingHours(30)
                .build();
        YearlyWorkload yearly = YearlyWorkload.builder()
                .year(2024)
                .list(List.of(monthly))
                .build();
        ResponseSummary summary = ResponseSummary.builder()
                .username("Tony.Smith")
                .firstName("Tony")
                .lastName("Smith")
                .status(true)
                .list(List.of(yearly))
                .build();
        when(service.getSummary(anyString())).thenReturn(summary);
        mockMvc.perform(get("/api/v1/workload/summary/Tony.Smith"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"username\":\"Tony.Smith\"," +
                        "\"firstName\":\"Tony\",\"lastName\":\"Smith\",\"status\":true," +
                        "\"list\":[{\"year\":2024,\"list\":[{\"month\":\"OCTOBER\",\"workingHours\":30}]}]}"));
    }
}
