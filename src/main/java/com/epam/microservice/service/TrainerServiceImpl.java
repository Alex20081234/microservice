package com.epam.microservice.service;

import com.epam.microservice.domain.Trainer;
import com.epam.microservice.domain.Training;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.dto.MonthlyWorkload;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.YearlyWorkload;
import com.epam.microservice.repository.TrainerRepository;
import com.epam.microservice.repository.TrainingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private static final String NOT_VALID = "Invalid field inputted";
    private final TrainerRepository repository;
    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public void submitWorkloadChanges(Trainer trainer, Training training, ActionType type) {
        validateTrainer(trainer);
        validateTraining(training);
        if (repository.existsByUsername(trainer.getUsername())) {
            submitWorkloadChangesWithExistingTrainer(trainer.getUsername(), training, type);
        } else {
            if (type == ActionType.DELETE) {
                throw new IllegalArgumentException("Invalid action type for nonexistent trainer");
            }
            submitWorkloadChangesWithNonexistentTrainer(trainer, training);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseSummary getSummary(String username) {
        Trainer trainer = repository.getTrainerByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(NOT_VALID));
        ResponseSummary summary = ResponseSummary.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .status(trainer.getIsActive())
                .build();
        summary.setList(collectWorkload(trainer.getTrainings()));
        return summary;
    }

    private List<YearlyWorkload> collectWorkload(List<Training> trainings) {
        Map<YearMonth, Integer> map = trainings.stream()
                .collect(Collectors.groupingBy(
                        training -> YearMonth.from(training.getDate()),
                        Collectors.summingInt(Training::getDuration)
                ));
        return map.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().getYear(),
                        Collectors.mapping(
                                entry -> MonthlyWorkload.builder()
                                        .month(entry.getKey().getMonth())
                                        .workingHours(entry.getValue())
                                        .build(),
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .map(entry -> YearlyWorkload.builder()
                        .year(entry.getKey())
                        .list(entry.getValue())
                        .build()
                )
                .toList();
    }

    private void submitWorkloadChangesWithExistingTrainer(String username,
                                                          Training training, ActionType type) {
        if (type == ActionType.DELETE) {
            trainingRepository.deleteByDateAndDurationAndTrainer_Username(training.getDate(),
                    training.getDuration(), username);
        } else {
            training.setTrainer(repository.getTrainerByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException(NOT_VALID)));
            trainingRepository.save(training);
        }
    }

    private void submitWorkloadChangesWithNonexistentTrainer(Trainer trainer, Training training) {
        repository.save(trainer);
        training.setTrainer(trainer);
        trainingRepository.save(training);
    }

    private void validateTrainer(Trainer trainer) {
        List<String> fields = new ArrayList<>();
        fields.add(trainer.getUsername());
        fields.add(trainer.getFirstName());
        fields.add(trainer.getLastName());
        fields.forEach(s -> {
            if (s == null || s.isEmpty()) throw new IllegalArgumentException(NOT_VALID);
        });
        if (trainer.getIsActive() == null) throw new IllegalArgumentException(NOT_VALID);
    }

    private void validateTraining(Training training) {
        if (training.getDate() == null || training.getDuration() <= 0) {
            throw new IllegalArgumentException(NOT_VALID);
        }
    }
}
