package com.epam.microservice.domain;

import lombok.Builder;
import lombok.Data;
import java.time.Month;

@Data
@Builder
public class MonthlyWorkload {
    private Month month;
    private int workingHours;

    public void setWorkingHours(int hours) {
        workingHours += hours;
    }
}
