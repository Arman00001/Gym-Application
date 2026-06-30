package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.trainer.*;
import com.epam.gymapp.dto.training.TrainingDto;

import java.util.List;

public interface TrainerService {
    TrainerCreateResponse createTrainer(TrainerCreateDto trainerCreateDto);

    TrainerDto updateTrainer(TrainerUpdateDto trainerDto);

    TrainerDto getTrainerById(Long id);
    TrainerDto getTrainerByUsername(String username);
    List<TrainerDto> getNotAssignedToTrainee(String username);
    List<TrainingDto> searchTrainings(TrainerTrainingsSearchCriteria criteria);

    TrainerDto changeIsActiveStatus(String username);
}
