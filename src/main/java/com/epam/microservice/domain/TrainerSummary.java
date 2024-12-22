package com.epam.microservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "TrainerSummaries")
public class TrainerSummary {
    @Id
    private String id;
    @Field(name = "username")
    @Indexed(unique = true)
    private String username;
    @Field(name = "firstName")
    private String firstName;
    @Field(name = "lastName")
    private String lastName;
    @Field(name = "status")
    private boolean status;
    @Field(name = "workload")
    private List<YearlyWorkload> workload;
}
