package com.epam.microservice.service;

import com.epam.microservice.common.EntityNotFoundException;
import com.epam.microservice.domain.Trainer;
import com.epam.microservice.domain.Training;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.dto.MonthlyWorkload;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.YearlyWorkload;
import com.epam.microservice.repository.TrainerRepository;
import com.epam.microservice.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {
    private Training training;
    private Trainer trainer;
    @Mock
    private TrainerRepository repository;
    @Mock
    private TrainingRepository trainingRepository;
    @InjectMocks
    private TrainerServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        training = Training.builder()
                .date(LocalDate.of(2024, 10, 10))
                .duration(20)
                .build();
        trainer = Trainer.builder()
                .username("Tom.Smith")
                .firstName("Tom")
                .lastName("Smith")
                .isActive(true)
                .build();
    }

    @Test
    void submitWorkloadChangesWithExistingTrainerShouldTryToMakeChangesWhenActionAdd() {
        when(repository.existsByUsername(anyString())).thenReturn(true);
        when(repository.getTrainerByUsername(anyString())).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any())).thenReturn(null);
        service.submitWorkloadChanges(trainer, training, ActionType.ADD);
        training.setTrainer(trainer);
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void submitWorkloadChangesWithExistingTrainerShouldThrowEntityNotFoundExceptionWhenNonExistent() {
        when(repository.existsByUsername(anyString())).thenReturn(true);
        RuntimeException e = assertThrows(EntityNotFoundException.class, () -> service.submitWorkloadChanges(trainer, training, ActionType.ADD));
        assertEquals("Trainer with username Tom.Smith was not found", e.getMessage());
    }

    @Test
    void submitWorkloadChangesWithExistingTrainerShouldTryToMakeChangesWhenActionDelete() {
        when(repository.existsByUsername(anyString())).thenReturn(true);
        doNothing().when(trainingRepository).deleteByParameters(any(), anyInt(), anyString());
        service.submitWorkloadChanges(trainer, training, ActionType.DELETE);
        verify(trainingRepository, times(1))
                .deleteByParameters(LocalDate.of(2024, 10, 10), 20, "Tom.Smith");
    }

    @Test
    void submitWorkloadChangesWithNonexistentTrainerShouldTryToMakeChangesWhenActionAdd() {
        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.save(any())).thenReturn(null);
        when(trainingRepository.save(any())).thenReturn(null);
        service.submitWorkloadChanges(trainer, training, ActionType.ADD);
        training.setTrainer(trainer);
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void submitWorkloadChangesWithNonexistentTrainerShouldThrowIllegalArgumentExceptionWhenActionDelete() {
        when(repository.existsByUsername(anyString())).thenReturn(false);
        RuntimeException e = assertThrows(IllegalArgumentException.class, () -> service.submitWorkloadChanges(trainer, training, ActionType.DELETE));
        assertEquals("Invalid action type for nonexistent trainer", e.getMessage());
    }

    @Test
    void getSummaryShouldReturnSummary() {
        trainer.setTrainings(List.of(training));
        MonthlyWorkload monthly = MonthlyWorkload.builder()
                .month(Month.OCTOBER)
                .workingHours(20)
                .build();
        YearlyWorkload yearly = YearlyWorkload.builder()
                .year(2024)
                .list(List.of(monthly))
                .build();
        ResponseSummary expected = ResponseSummary.builder()
                .username("Tom.Smith")
                .firstName("Tom")
                .lastName("Smith")
                .status(true)
                .list(List.of(yearly))
                .build();
        when(repository.getTrainerByUsername(anyString())).thenReturn(Optional.of(trainer));
        ResponseSummary actual = service.getSummary("Tom.Smith");
        assertEquals(expected, actual);
    }

    @Test
    void getSummaryShouldThrowEntityNotFoundExceptionWhenNonExistentTrainer() {
        RuntimeException e = assertThrows(EntityNotFoundException.class, () -> service.getSummary("Non.Existent"));
        assertEquals("Trainer with username Non.Existent was not found", e.getMessage());
    }
}
