package com.epam.microservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.REMOVE)
    private List<Training> trainings;
}
