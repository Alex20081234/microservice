package com.epam.microservice.controller;

import com.epam.microservice.dto.*;
import com.epam.microservice.service.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

class WorkloadControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private MockMvc mockMvc;

    @Mock
    private TrainerService service;

    @InjectMocks
    private WorkloadController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void submitWorkloadChangesShouldTryToProcessChanges() throws Exception {
        RequestParams params = RequestParams.builder()
                .username("Jane.Doe")
                .firstName("Jane")
                .lastName("Doe")
                .date(LocalDate.now())
                .duration(30)
                .type(ActionType.ADD)
                .build();
        doNothing().when(service).submitWorkloadChanges(any(), any(), any());
        mockMvc.perform(patch("/api/v1/workload/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params)))
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
