package com.epam.microservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class YearlyWorkload {
    private List<MonthlyWorkload> list;
}
