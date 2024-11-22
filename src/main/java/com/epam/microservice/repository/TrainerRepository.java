package com.epam.microservice.repository;

import com.epam.microservice.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
}
