package com.epam.gymapp.persistence.repository.training;

import com.epam.gymapp.persistence.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing and managing {@link Training} entities.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    Training deleteTrainingById(Long id);
}
