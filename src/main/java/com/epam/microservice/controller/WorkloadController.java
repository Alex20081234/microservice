package com.epam.microservice.controller;

import com.epam.microservice.domain.Trainer;
import com.epam.microservice.domain.Training;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import com.epam.microservice.service.TrainerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workload")
@AllArgsConstructor
public class WorkloadController {
    private final TrainerService service;

    @PatchMapping("/submit")
    public ResponseEntity<Void> submitWorkloadChanges(@Valid @RequestBody SubmitWorkloadChangesRequestBody requestBody) {
        Trainer trainer = Trainer.builder()
                .username(requestBody.getTrainerUsername())
                .firstName(requestBody.getTrainerFirstName())
                .lastName(requestBody.getTrainerLastName())
                .isActive(requestBody.getTrainerIsActive())
                .build();
        Training training = Training.builder()
                .date(requestBody.getTrainingDate())
                .duration(requestBody.getTrainingDurationMinutes())
                .build();
        service.submitWorkloadChanges(trainer, training, requestBody.getChangeType());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/{username}")
    public ResponseEntity<ResponseSummary> getSummary(@PathVariable String username) {
        return ResponseEntity.ok(service.getSummary(username));
    }
}
