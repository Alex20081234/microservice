package com.epam.microservice.repository;

import com.epam.microservice.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {
    @Modifying
    @Query("DELETE FROM Training t WHERE t.date = :date AND t.duration = :duration AND t.trainer.username = :username")
    void deleteByParameters(LocalDate date, int duration, String username);
}
