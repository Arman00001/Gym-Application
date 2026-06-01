package com.epam.gymapp.persistence.repository.trainee_trainer;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;

import java.util.List;

public interface TraineeTrainerRepository {
    void updateTrainerList(Trainee trainee, List<Trainer> trainers);
}
