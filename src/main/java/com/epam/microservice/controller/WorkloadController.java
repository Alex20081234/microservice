package com.epam.microservice.controller;

import com.epam.microservice.dto.RequestTrainer;
import com.epam.microservice.dto.ResponseSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workload")
public class WorkloadController {

    @PatchMapping("/submit")
    public ResponseEntity<Void> submitWorkloadChanges(@RequestBody RequestTrainer requestTrainer) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/{username}")
    public ResponseEntity<ResponseSummary> getSummary(@PathVariable String username) {
        ResponseSummary summary = ResponseSummary.builder().build();
        return ResponseEntity.ok(summary);
    }
}
