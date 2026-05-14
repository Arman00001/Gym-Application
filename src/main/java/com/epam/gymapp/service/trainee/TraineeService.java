package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;

public interface TraineeService {
    TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto);
    TraineeDto updateTrainee(TraineeUpdateDto traineeCreateDto);
    void deleteTrainee(String username);
    TraineeDto getTrainee(String username);
}
