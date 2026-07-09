package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for converting between {@link Trainee} entities and trainee DTOs.
 *
 * <p>
 * Includes custom mapping logic for building trainee DTOs with assigned trainers.
 * </p>
 */
@Mapper
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    Trainee mapCreateToTrainee(TraineeCreateDto traineeCreateDto);

    @Mapping(target = "trainers", ignore = true)
    TraineeDto mapToDto(Trainee trainee, User user);

    default TraineeDto mapToFullDto(Trainee trainee, List<Trainer> trainers) {
        TraineeDto traineeDto = mapToDto(trainee, trainee.getUser());

        traineeDto.setTrainers(
                trainers.stream().map(trainer -> {
                    TraineeTrainerDto dto = new TraineeTrainerDto();
                    dto.setFirstName(trainer.getUser().getFirstName());
                    dto.setLastName(trainer.getUser().getLastName());
                    dto.setUsername(trainer.getUser().getUsername());
                    dto.setSpecialization(trainer.getSpecialization().getName());
                    return dto;
                }).toList()
        );
        return traineeDto;
    }

    Trainee mapUpdateToTrainee(TraineeUpdateDto traineeUpdateDto);

    @Mapping(target = "password", source = "rawPassword")
    TraineeCreateResponse mapToCreateResponse(User user, String rawPassword);
}
