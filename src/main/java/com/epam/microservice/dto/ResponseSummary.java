package com.epam.microservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseSummary {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean status;
    private List<YearlyWorkload> list;
}
