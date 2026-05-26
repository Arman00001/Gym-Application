package com.epam.gymapp.persistence.repository.training;

import com.epam.gymapp.persistence.entity.Training;

import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);
    Optional<Training> get(Long id);
}
