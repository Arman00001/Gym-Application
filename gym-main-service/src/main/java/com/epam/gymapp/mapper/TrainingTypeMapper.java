package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateResponse;
import com.epam.gymapp.dto.trainingtype.TrainingTypeDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeUpdateDto;
import com.epam.gymapp.persistence.entity.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TrainingTypeMapper {
    TrainingTypeMapper INSTANCE = Mappers.getMapper(TrainingTypeMapper.class);

    TrainingType mapCreateToTrainingType(TrainingTypeCreateDto trainingTypeCreateDto);

    TrainingTypeCreateResponse mapToCreateResponse(TrainingType trainingType);

    TrainingTypeDto mapToDto(TrainingType trainingType);

    TrainingType mapUpdateToTrainingType(TrainingTypeUpdateDto trainingTypeUpdateDto);

    List<TrainingTypeDto> mapToDtoList(List<TrainingType> all);
}
