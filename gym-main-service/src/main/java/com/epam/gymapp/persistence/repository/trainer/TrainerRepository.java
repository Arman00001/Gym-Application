package com.epam.gymapp.persistence.repository.trainer;

import com.epam.gymapp.dto.trainer.TrainerTrainingsSearchCriteria;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing and managing {@link Trainer} entities.
 *
 * <p>
 * Provides custom queries for retrieving trainers by username, finding trainers
 * not assigned to a trainee, and searching trainer trainings by criteria.
 * </p>
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Long> {
    @Query("SELECT t FROM Trainer t WHERE t.user.username = :username")
    Optional<Trainer> getByUsername(String username);

    @Query("""
        SELECT t FROM Trainer t
        WHERE t.id NOT IN (
        SELECT assignedTrainer.id FROM TraineeTrainer tt JOIN tt.trainer assignedTrainer
        WHERE t.user.username = :username AND t.user.isActive = true)""")
    List<Trainer> getNotAssignedToTrainee(String username);

    @Query("""
    SELECT training
    FROM Training training
    WHERE training.trainer.user.username = :username
    AND (:#{#criteria.traineeFirstName} IS NULL OR LOWER(training.trainee.user.firstName) LIKE LOWER(CONCAT('%', :#{#criteria.traineeFirstName}, '%')))
    AND (:#{#criteria.traineeLastName} IS NULL OR LOWER(training.trainee.user.lastName) LIKE LOWER(CONCAT('%', :#{#criteria.traineeLastName}, '%')))
    AND (:#{#criteria.fromDate} IS NULL OR training.date >= :#{#criteria.fromDate})
    AND (:#{#criteria.toDate} IS NULL OR training.date <= :#{#criteria.toDate})""")
    List<Training> getTrainingsByCriteria(
            @Param("criteria") TrainerTrainingsSearchCriteria criteria,
            @Param("username") String username
    );

    @Query("SELECT t FROM Trainer t WHERE t.user.username IN :trainerUsernames")
    List<Trainer> getByUsernames(List<String> trainerUsernames);

    @Query("""
        SELECT DISTINCT tt.trainer
        FROM TraineeTrainer tt
        WHERE tt.trainee.user.username = :traineeUsername""")
    List<Trainer> getAllByTraineeUsername(String traineeUsername);
}
