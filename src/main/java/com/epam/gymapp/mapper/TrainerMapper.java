package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainer.*;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(target = "specialization", ignore = true)
    Trainer mapToTrainer(TrainerCreateDto trainerCreateDto);

    @Mapping(target = "specialization", source = "trainingType.name")
    TrainerDto mapToDto(Trainer trainer, User user, TrainingType trainingType);

    @Mapping(target = "specialization", ignore = true)
    Trainer mapUpdateToTrainer(TrainerUpdateDto dto);

    @Mapping(target = "password", source = "rawPassword")
    TrainerCreateResponse mapToCreateResponse(User user, String rawPassword);

    default List<TrainerDto> mapToDtoList(List<Trainer> trainers){
        if(trainers == null){
            return Collections.emptyList();
        }

        return trainers.stream()
                .map(trainer -> mapToDto(trainer, trainer.getUser(), trainer.getSpecialization()))
                .toList();
    };

    default TrainerDto mapToFullDto(Trainer trainer, List<Trainee> trainees){
        TrainerDto trainerDto = mapToDto(trainer,trainer.getUser(), trainer.getSpecialization());

        trainerDto.setTrainees(
                trainees.stream().map(trainee -> {
                    TrainerTraineeDto dto = new TrainerTraineeDto();
                    dto.setFirstName(trainee.getUser().getFirstName());
                    dto.setLastName(trainee.getUser().getLastName());
                    dto.setUsername(trainee.getUser().getUsername());
                    return dto;
                }).toList()
        );
        return trainerDto;
    }
}
