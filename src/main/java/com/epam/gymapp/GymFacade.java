package com.epam.gymapp;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.trainer.*;
import com.epam.gymapp.dto.training.*;
import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.service.trainee.TraineeService;
import com.epam.gymapp.service.trainer.TrainerService;
import com.epam.gymapp.service.training.TrainingService;
import com.epam.gymapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;

    public TraineeCreateResponse createTrainee(TraineeCreateDto dto) {
        return traineeService.createTrainee(dto);
    }

    public TraineeDto updateTrainee(TraineeUpdateDto dto) {
        return traineeService.updateTrainee(dto);
    }

    public void deleteTrainee(DeleteRequestDto dto) {
        traineeService.deleteTraineeByUsername(dto);
    }

    public TraineeDto getTrainee(AuthenticationRequestDto dto) {
        return traineeService.getTraineeByUsername(dto);
    }

    public TraineeDto changeTraineeActiveStatus(AuthenticationRequestDto dto) {
        return traineeService.changeIsActiveStatus(dto);
    }

    public List<TrainingDto> getTraineeTrainings(TraineeTrainingsSearchCriteria criteria) {
        return traineeService.searchTrainings(criteria);
    }

//    public TraineeDto updateTraineeTrainers(TraineeTrainerListUpdateDto dto) {
//        return traineeService.updateTrainerList(dto);
//    }

    public TrainerCreateResponse createTrainer(TrainerCreateDto dto) {
        return trainerService.createTrainer(dto);
    }

    public TrainerDto updateTrainer(TrainerUpdateDto dto) {
        return trainerService.updateTrainer(dto);
    }

    public TrainerDto getTrainer(AuthenticationRequestDto dto) {
        return trainerService.getTrainerByUsername(dto);
    }

    public TrainerDto changeTrainerActiveStatus(AuthenticationRequestDto dto) {
        return trainerService.changeIsActiveStatus(dto);
    }

    public List<TrainingDto> getTrainerTrainings(TrainerTrainingsSearchCriteria criteria) {
        return trainerService.searchTrainings(criteria);
    }

    public List<TrainerDto> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerService.getNotAssignedToTrainee(traineeUsername);
    }

    public TrainingDto createTraining(TrainingCreateDto dto) {
        return trainingService.createTraining(dto);
    }

    public TrainingDto getTraining(Long id) {
        return trainingService.getTraining(id);
    }

    public boolean isAuthenticated(AuthenticationRequestDto dto) {
        return userService.isAuthenticated(dto.getUsername(), dto.getPassword());
    }

    public void changePassword(ChangePasswordRequestDto dto) {
        userService.changePassword(dto);
    }
}