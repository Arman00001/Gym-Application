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

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    @Query("SELECT t FROM Trainee t WHERE t.user.username = :username")
    Optional<Trainee> getByUsername(String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Trainee t WHERE t.user.username = :username")
    void deleteByUsername(String username);

    @Query("""
        SELECT trainings
        FROM Training trainings
        WHERE trainings.trainee.user.username = :username
        AND (:firstName IS NULL OR LOWER(trainings.trainer.user.firstName) LIKE LOWER(CONCAT('%',CAST(:firstName AS string),'%')))
        AND (:lastName IS NULL OR LOWER(trainings.trainer.user.lastName) LIKE LOWER(CONCAT('%',CAST(:lastName AS string),'%')))
        AND (:trainingType IS NULL OR LOWER(trainings.type.name) LIKE LOWER(CONCAT('%',CAST(:trainingType AS string),'%')))
        AND (:fromDate IS NULL OR trainings.date >= :fromDate)
        AND (:toDate IS NULL OR trainings.date <= :toDate)""")
    List<Training> getTrainingsByCriteria(TraineeTrainingsSearchCriteria criteria);

    @Query("""
        SELECT DISTINCT tt.trainee
        FROM TraineeTrainer tt
        WHERE tt.trainer.user.username = :trainerUsername""")
    List<Trainee> getAllByTrainerUsername(String trainerUsername);


//    Trainee save(Trainee trainee);
//
//    Trainee update(Trainee trainee);
//
//    Trainee delete(Long id);
//    void deleteByUserId(Long userId);
//
//    List<Trainee> getAll();
//    Optional<Trainee> getByUserId(Long id);
//    Optional<Trainee> get(Long id);
//

}
