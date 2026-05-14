package com.epam.gymapp.persistence.repository.training;

import com.epam.gymapp.persistence.entity.Training;

public interface TrainingRepository {
    Training save(Training training);
    Training get(String trainerUsername);
}
