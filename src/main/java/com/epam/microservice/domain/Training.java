package com.epam.microservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "TrainerId", nullable = false)
    private Trainer trainer;

    @Column(name = "training_date", nullable = false)
    private LocalDate date;

    @Column(name = "training_duration", nullable = false)
    private int duration;
}
