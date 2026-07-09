package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.dto.trainee.TraineeTrainingsSearchCriteria;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing and managing {@link Trainee} entities.
 *
 * <p>
 * Provides custom queries for retrieving trainees by username, deleting trainee
 * profiles by username, searching trainee trainings by criteria, and retrieving
 * trainees assigned to a trainer.
 * </p>
 */
@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    @Query("SELECT t FROM Trainee t WHERE t.user.username = :username")
    Optional<Trainee> getByUsername(String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Trainee t WHERE t.user.username = :username")
    void deleteByUsername(String username);

    @Query("""
        SELECT training
        FROM Training training
        WHERE training.trainee.user.username = :username
        AND (:#{#criteria.trainerFirstName} IS NULL OR LOWER(training.trainer.user.firstName) LIKE LOWER(CONCAT('%', :#{#criteria.trainerFirstName}, '%')))
        AND (:#{#criteria.trainerLastName} IS NULL OR LOWER(training.trainer.user.lastName) LIKE LOWER(CONCAT('%', :#{#criteria.trainerLastName}, '%')))
        AND (:#{#criteria.trainingType} IS NULL OR LOWER(training.type.name) LIKE LOWER(CONCAT('%', :#{#criteria.trainingType}, '%')))
        AND (:#{#criteria.fromDate} IS NULL OR training.date >= :#{#criteria.fromDate})
        AND (:#{#criteria.toDate} IS NULL OR training.date <= :#{#criteria.toDate})""")
    List<Training> getTrainingsByCriteria(TraineeTrainingsSearchCriteria criteria, String username);

    @Query("""
        SELECT DISTINCT tt.trainee
        FROM TraineeTrainer tt
        WHERE tt.trainer.user.username = :trainerUsername""")
    List<Trainee> getAllByTrainerUsername(String trainerUsername);
}
