package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.training.TrainingDto;

import java.util.List;

public interface TraineeService {
    TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto);

    TraineeDto updateTrainee(String username, TraineeUpdateDto traineeCreateDto);

    void deleteTraineeByUsername(DeleteRequestDto dto);

    TraineeDto getTraineeByUsername(String username);
    List<TrainingDto> searchTrainings(TraineeTrainingsSearchCriteria criteria);

    TraineeDto changeIsActiveStatus(String username);

}
