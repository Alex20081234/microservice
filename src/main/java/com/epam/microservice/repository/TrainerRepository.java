package com.epam.microservice.repository;

import com.epam.microservice.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Boolean existsByUsername(String username);

    Optional<Trainer> getTrainerByUsername(String username);
}
