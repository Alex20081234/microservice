package com.epam.microservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitWorkloadChangesRequestBody {

    @NotBlank(message = "Trainer username must not be blank")
    private String trainerUsername;

    @NotBlank(message = "Trainer first name must not be blank")
    private String trainerFirstName;

    @NotBlank(message = "Trainer last name must not be blank")
    private String trainerLastName;

    @NotNull(message = "Trainer active status must not be null")
    private Boolean trainerIsActive;

    @NotNull(message = "Training date must not be null")
    private LocalDate trainingDate;

    @Min(value = 1, message = "Training duration must be a positive number")
    private int trainingDurationMinutes;

    @NotNull(message = "Change type must not be null")
    private ActionType changeType;
}
