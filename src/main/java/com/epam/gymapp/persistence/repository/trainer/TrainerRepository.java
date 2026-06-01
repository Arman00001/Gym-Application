package com.epam.gymapp.persistence.repository.trainer;

import com.epam.gymapp.dto.trainer.TrainerTrainingsSearchCriteria;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);

    Trainer update(Trainer trainer);

    Optional<Trainer> get(Long id);
    Optional<Trainer> getByUserId(Long userId);
    List<Trainer> getAll();
    Optional<Trainer> getByUsername(String username);
    List<Trainer> getByUsernames(List<String> trainerUsernames);
    List<Training> getTrainingsByCriteria(TrainerTrainingsSearchCriteria criteria);
    List<Trainer> getNotAssignedToTrainee(String username);
    List<Trainer> getAllByTraineeUsername(String traineeUsername);

    Trainer changeIsActiveStatus(String username);

}
