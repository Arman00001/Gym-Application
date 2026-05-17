package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    Trainer mapToTrainer(TrainerCreateDto trainerCreateDto);

    TrainerDto mapToDto(Trainer trainer, User user);

    Trainer mapUpdateToTrainer(TrainerUpdateDto dto);

    TrainerCreateResponse mapToCreatedDto(Trainer trainerResult);
}
