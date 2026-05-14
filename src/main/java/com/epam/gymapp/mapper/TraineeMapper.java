package com.epam.gymapp.mapper;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.persistence.entity.Trainee;

public class TraineeMapper {
    public static Trainee mapCreateToTrainee(TraineeCreateDto dto) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());

        return trainee;
    }

    public static TraineeDto mapToDto(Trainee trainee) {
        TraineeDto dto = new TraineeDto();
        dto.setAddress(trainee.getAddress());
        dto.setIsActive(trainee.getIsActive());
        dto.setDateOfBirth(trainee.getDateOfBirth());
        dto.setFirstName(trainee.getFirstName());
        dto.setLastName(trainee.getLastName());

        return dto;
    }

    public static Trainee mapUpdateToTrainee(TraineeUpdateDto dto) {
        Trainee trainee = new Trainee();
        trainee.setUsername(dto.getUsername());
        trainee.setIsActive(dto.getIsActive());
        trainee.setFirstName(dto.getFirstName());
        trainee.setLastName(dto.getLastName());
        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());

        return trainee;
    }

    public static TraineeCreateResponse mapToCreatedDto(Trainee traineeResult) {
        TraineeCreateResponse response = new TraineeCreateResponse();
        response.setUsername(traineeResult.getUsername());
        response.setPassword(traineeResult.getPassword());

        return response;
    }
}
