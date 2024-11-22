package com.epam.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTrainer {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private LocalDate date;
    private int duration;
    private ActionType type;
}
