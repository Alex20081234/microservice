package com.epam.microservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearlyWorkload {
    private int year;
    private List<MonthlyWorkload> list;

    public void add(MonthlyWorkload... workload) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (workload != null) {
            for (MonthlyWorkload current : workload) {
                if (current != null) {
                    list.add(current);
                }
            }
        }
    }
}
