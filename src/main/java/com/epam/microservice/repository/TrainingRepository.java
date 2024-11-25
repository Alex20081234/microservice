package com.epam.microservice.repository;

import com.epam.microservice.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {
    void deleteByDateAndDurationAndTrainer_Username(LocalDate date, int duration, String username);
}
