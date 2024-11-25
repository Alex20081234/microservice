package com.epam.microservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Month;

@Data
@Builder
public class MonthlyWorkload {
    private Month month;
    private int workingHours;
}
