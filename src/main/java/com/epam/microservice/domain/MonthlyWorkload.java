package com.epam.microservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Month;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyWorkload {
    private Month month;
    private int workingHours;

    public void setWorkingHours(int hours) {
        workingHours += hours;
    }
}
