package com.epam.microservice.dto;

import com.epam.microservice.domain.YearlyWorkload;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseSummary {
    private String username;
    private String firstName;
    private String lastName;
    private boolean status;
    private List<YearlyWorkload> list;
}
