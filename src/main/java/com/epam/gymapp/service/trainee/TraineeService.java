package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.training.TrainingDto;

import java.util.List;

public interface TraineeService {
    TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto);

    TraineeDto updateTrainee(TraineeUpdateDto traineeCreateDto);

    void deleteTrainee(DeleteRequestDto dto);
    void deleteTraineeByUsername(DeleteRequestDto dto);

    TraineeDto getTraineeById(Long id);
    TraineeDto getTraineeByUsername(String username);
    List<TrainingDto> searchTrainings(TraineeTrainingsSearchCriteria criteria);

    TraineeDto changeIsActiveStatus(String username);

}
