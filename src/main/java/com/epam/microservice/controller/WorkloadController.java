package com.epam.microservice.controller;

import com.epam.microservice.domain.Trainer;
import com.epam.microservice.domain.Training;
import com.epam.microservice.dto.RequestParams;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.service.TrainerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workload")
@AllArgsConstructor
public class WorkloadController {
    private final TrainerService service;

    @PatchMapping("/submit")
    public ResponseEntity<Void> submitWorkloadChanges(@RequestBody RequestParams requestParams) {
        Trainer trainer = Trainer.builder()
                .username(requestParams.getUsername())
                .firstName(requestParams.getFirstName())
                .lastName(requestParams.getLastName())
                .isActive(requestParams.getIsActive())
                .build();
        Training training = Training.builder()
                .date(requestParams.getDate())
                .duration(requestParams.getDuration())
                .build();
        service.submitWorkloadChanges(trainer, training, requestParams.getType());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/{username}")
    public ResponseEntity<ResponseSummary> getSummary(@PathVariable String username) {
        return ResponseEntity.ok(service.getSummary(username));
    }
}
