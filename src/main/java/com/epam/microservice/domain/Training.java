package com.epam.microservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "TrainerId", nullable = false)
    private Trainer trainer;

    @Column(name = "training_date", nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDate date;

    @Column(name = "training_duration", nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private int duration;
}
