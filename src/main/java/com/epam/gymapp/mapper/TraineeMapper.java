package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    Trainee mapCreateToTrainee(TraineeCreateDto traineeCreateDto);

    TraineeDto mapToDto(Trainee trainee, User user);

    Trainee mapUpdateToTrainee(TraineeUpdateDto traineeUpdateDto);

    TraineeCreateResponse mapToCreatedDto(Trainee traineeResult);
}
