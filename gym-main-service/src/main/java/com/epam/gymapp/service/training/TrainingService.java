package com.epam.gymapp.service.training;


import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;

public interface TrainingService {
    TrainingDto createTraining(TrainingCreateDto trainingCreateDto);
    TrainingDto getTraining(Long id);
}
