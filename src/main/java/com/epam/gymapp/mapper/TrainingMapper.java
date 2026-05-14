package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.persistence.entity.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {
    public static Training mapCreateToTraining(TrainingCreateDto dto) {
        Training training = new Training();
        training.setDate(dto.getDate());
        training.setName(dto.getName());
        training.setDuration(dto.getDuration());
        training.setTrainerUsername(dto.getTrainerUsername());
        training.setTraineeUsername(dto.getTraineeUsername());
        training.setType(dto.getType());
        training.setName(dto.getName());

        return training;
    }

    public static TrainingDto mapToDto(Training training){
        TrainingDto dto = new TrainingDto();
        dto.setDate(training.getDate());
        dto.setName(training.getName());
        dto.setDuration(training.getDuration());
        dto.setTrainerUsername(training.getTrainerUsername());
        dto.setTraineeUsername(training.getTraineeUsername());
        dto.setName(training.getName());
        dto.setType(training.getType());
        dto.setId(training.getId());

        return dto;
    }
}
