package com.epam.microservice.service;

import com.epam.microservice.common.EntityNotFoundException;
import com.epam.microservice.domain.MonthlyWorkload;
import com.epam.microservice.domain.TrainerSummary;
import com.epam.microservice.domain.YearlyWorkload;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import com.epam.microservice.dao.TrainerSummariesDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerSummariesServiceImplTest {
    private final MonthlyWorkload monthlyWorkload = MonthlyWorkload.builder()
            .month(Month.DECEMBER)
            .workingHours(60)
            .build();
    private final YearlyWorkload yearlyWorkload = YearlyWorkload.builder()
            .year(2024)
            .list(List.of(monthlyWorkload))
            .build();
    private SubmitWorkloadChangesRequestBody body;
    @Mock
    private TrainerSummariesDao dao;
    @InjectMocks
    private TrainerSummariesServiceImpl service;

    @BeforeEach
    void setUp() {
        body = SubmitWorkloadChangesRequestBody.builder()
                .trainerUsername("Jane.Doe")
                .trainerFirstName("Jane")
                .trainerLastName("Doe")
                .trainerIsActive(true)
                .trainingDate(LocalDate.of(2024, 12, 12))
                .trainingDurationMinutes(60)
                .changeType(ActionType.ADD)
                .build();
    }

    @Test
    void submitWorkloadChangesShouldTryToMakeChanges() {
        when(dao.exists(anyString())).thenReturn(true);
        doNothing().when(dao).updateOrSave(any());
        service.submitWorkloadChanges(body);
        TrainerSummary expected = TrainerSummary.builder()
                .username(body.getTrainerUsername())
                .firstName(body.getTrainerFirstName())
                .lastName(body.getTrainerLastName())
                .status(body.getTrainerIsActive())
                .workloads(List.of(yearlyWorkload))
                .build();
        verify(dao, times(1)).updateOrSave(expected);
        when(dao.exists(anyString())).thenReturn(true);
        doNothing().when(dao).updateOrSave(any());
        body.setChangeType(ActionType.DELETE);
        service.submitWorkloadChanges(body);
        verify(dao, times(1)).updateOrSave(expected);
        when(dao.exists(anyString())).thenReturn(false);
        doNothing().when(dao).updateOrSave(any());
        body.setChangeType(ActionType.ADD);
        body.setTrainerUsername("Nonexistent");
        service.submitWorkloadChanges(body);
        verify(dao, times(1)).updateOrSave(expected);
    }

    @Test
    void submitWorkloadChangesShouldThrowIllegalArgumentExceptionWhenNonExistentAndActionDelete() {
        when(dao.exists(anyString())).thenReturn(false);
        body.setTrainerUsername("Nonexistent");
        body.setChangeType(ActionType.DELETE);
        RuntimeException e = assertThrows(IllegalArgumentException.class, () -> service.submitWorkloadChanges(body));
        assertEquals("Invalid action type for nonexistent trainer", e.getMessage());
    }

    @Test
    void getSummaryShouldThrowEntityNotFoundExceptionWhenSummaryNotFound() {
        RuntimeException e = assertThrows(EntityNotFoundException.class, () -> service.getSummary("Nonexistent"));
        assertEquals("Trainer summary for username Nonexistent was not found", e.getMessage());
    }

    @Test
    void getSummaryShouldReturnSummary() {
        TrainerSummary summary = TrainerSummary.builder()
                .id("12345")
                .username(body.getTrainerUsername())
                .firstName(body.getTrainerFirstName())
                .lastName(body.getTrainerLastName())
                .status(body.getTrainerIsActive())
                .workloads(List.of(yearlyWorkload))
                .build();
        when(dao.getTrainerSummary(anyString())).thenReturn(Optional.of(summary));
        ResponseSummary expected = ResponseSummary.builder()
                .username(body.getTrainerUsername())
                .firstName(body.getTrainerFirstName())
                .lastName(body.getTrainerLastName())
                .status(body.getTrainerIsActive())
                .list(List.of(yearlyWorkload))
                .build();
        assertEquals(expected, service.getSummary("Jane.Doe"));
    }
}
