package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.persistence.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for converting between {@link Training} entities and training DTOs.
 */
@Mapper
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    Training mapCreateToTraining(TrainingCreateDto dto);

    @Mapping(target = "trainerUsername", source = "training.trainer.user.username")
    @Mapping(target = "traineeUsername", source = "training.trainee.user.username")
    TrainingDto mapToDto(Training training);

    List<TrainingDto> mapToDtoList(List<Training> trainingsByCriteria);
}
