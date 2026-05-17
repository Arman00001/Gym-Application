package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.persistence.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    Training mapCreateToTraining(TrainingCreateDto dto);

    TrainingDto mapToDto(Training training);
}
