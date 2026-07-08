package com.epam.gymapp.workload.persistence.repository;

import com.epam.gymapp.workload.persistence.entity.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    Optional<TrainerWorkload> findByUsernameAndYearAndMonth(String username, Integer year, Integer month);
}
