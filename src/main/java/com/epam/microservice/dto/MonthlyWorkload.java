package com.epam.microservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyWorkload {
    private int workingHours;
}
