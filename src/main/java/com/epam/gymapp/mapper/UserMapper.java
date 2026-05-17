package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.persistence.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserCreateDto traineeToCreateUser(TraineeCreateDto traineeCreateDto);

    UserCreateDto trainerToCreateUser(TrainerCreateDto trainerCreateDto);

    UserUpdateDto traineeToUpdateUser(TraineeUpdateDto traineeUpdateDto);

    UserUpdateDto trainerToUpdateUser(TrainerUpdateDto trainerUpdateDto);

    User userCreateToUser(UserCreateDto userCreateDto);
}
