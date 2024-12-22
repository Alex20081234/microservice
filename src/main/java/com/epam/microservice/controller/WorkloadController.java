package com.epam.microservice.controller;

import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import com.epam.microservice.service.TrainerSummariesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workload")
@AllArgsConstructor
public class WorkloadController {
    private final TrainerSummariesService service;

    @PatchMapping("/submit")
    public ResponseEntity<Void> submitWorkloadChanges(@Valid @RequestBody SubmitWorkloadChangesRequestBody requestBody) {
        service.submitWorkloadChanges(requestBody);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/{username}")
    public ResponseEntity<ResponseSummary> getSummary(@PathVariable String username) {
        return ResponseEntity.ok(service.getSummary(username));
    }
}
