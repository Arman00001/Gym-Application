package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    Trainee mapCreateToTrainee(TraineeCreateDto traineeCreateDto);

    @Mapping(target = "trainers", ignore = true)
    TraineeDto mapToDto(Trainee trainee, User user);

    default TraineeDto mapToFullDto(Trainee trainee){
        TraineeDto traineeDto = mapToDto(trainee,trainee.getUser());

        traineeDto.setTrainers(
                trainee.getTrainers().stream().map(trainer -> {
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

    TraineeCreateResponse mapToCreateResponse(User user);
}
