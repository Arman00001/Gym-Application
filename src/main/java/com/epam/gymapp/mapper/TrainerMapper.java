package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.persistence.entity.Trainer;

public class TrainerMapper {
    public static Trainer mapToTrainer(TrainerCreateDto trainerCreateDto) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(trainerCreateDto.getFirstName());
        trainer.setLastName(trainerCreateDto.getLastName());
        trainer.setSpecialization(trainerCreateDto.getSpecialization());

        return trainer;
    }

    public static TrainerDto mapToDto(Trainer trainer) {
        TrainerDto dto = new TrainerDto();
        dto.setIsActive(trainer.getIsActive());
        dto.setSpecialization(trainer.getSpecialization());
        dto.setFirstName(trainer.getFirstName());
        dto.setLastName(trainer.getLastName());
        dto.setUsername(trainer.getUsername());

        return dto;
    }

    public static Trainer mapUpdateToTrainer(TrainerUpdateDto dto) {
        Trainer trainer = new Trainer();
        trainer.setUsername(dto.getUsername());
        trainer.setIsActive(dto.getIsActive());
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        trainer.setSpecialization(dto.getSpecialization());

        return trainer;
    }

    public static TrainerCreateResponse mapToCreatedDto(Trainer trainerResult) {
        TrainerCreateResponse response = new TrainerCreateResponse();
        response.setUsername(trainerResult.getUsername());
        response.setPassword(trainerResult.getPassword());

        return response;
    }
}
