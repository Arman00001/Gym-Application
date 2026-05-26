package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(target = "specialization", ignore = true)
    Trainer mapToTrainer(TrainerCreateDto trainerCreateDto);

    @Mapping(target = "specialization", source = "trainingType.name")
    TrainerDto mapToDto(Trainer trainer, User user, TrainingType trainingType);

    @Mapping(target = "specialization", ignore = true)
    Trainer mapUpdateToTrainer(TrainerUpdateDto dto);

    TrainerCreateResponse mapToCreateResponse(User user);
}
