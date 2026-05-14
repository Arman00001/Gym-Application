package com.epam.gymapp;

import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.trainer.*;
import com.epam.gymapp.dto.training.*;
import com.epam.gymapp.service.trainee.TraineeService;
import com.epam.gymapp.service.trainer.TrainerService;
import com.epam.gymapp.service.training.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TraineeCreateResponse createTrainee(TraineeCreateDto dto) {
        return traineeService.createTrainee(dto);
    }

    public TraineeDto updateTrainee(TraineeUpdateDto dto) {
        return traineeService.updateTrainee(dto);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    public TraineeDto getTrainee(String username) {
        return traineeService.getTrainee(username);
    }

    public TrainerCreateResponse createTrainer(TrainerCreateDto dto) {
        return trainerService.createTrainer(dto);
    }

    public TrainerDto updateTrainer(TrainerUpdateDto dto) {
        return trainerService.updateTrainer(dto);
    }

    public TrainerDto getTrainer(String username) {
        return trainerService.getTrainer(username);
    }

    public TrainingDto createTraining(TrainingCreateDto dto) {
        return trainingService.createTraining(dto);
    }

    public TrainingDto getTraining(Long id) {
        return trainingService.getTraining(id);
    }
}