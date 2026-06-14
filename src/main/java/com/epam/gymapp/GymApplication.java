package com.epam.gymapp;

import com.epam.gymapp.configuration.AppConfig;
import com.epam.gymapp.configuration.RepoConfig;
import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeTrainerListUpdateDto;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class GymApplication {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class, RepoConfig.class)) {

            GymFacade facade = context.getBean(GymFacade.class);

            TraineeCreateDto dto = new TraineeCreateDto();
            dto.setFirstName("Test");
            dto.setLastName("User");
            dto.setDateOfBirth(LocalDate.parse("2000-01-01"));
            dto.setAddress("Test address");

            TraineeCreateResponse response = facade.createTrainee(dto);

            System.out.println("Created trainee:");
            System.out.println("username = " + response.getUsername());
            System.out.println("password = " + response.getPassword());

            AuthenticationRequestDto auth = new AuthenticationRequestDto();
            auth.setUsername(response.getUsername());
            auth.setPassword(response.getPassword());

            TraineeDto created = facade.getTrainee(auth);
            TraineeTrainerListUpdateDto traineeTrainerListUpdateDto =
                    new TraineeTrainerListUpdateDto();
            traineeTrainerListUpdateDto.setUsername(response.getUsername());
            traineeTrainerListUpdateDto.setPassword(response.getPassword());
            traineeTrainerListUpdateDto.setTrainerUsernames(List.of("Alex.Johnson","Emma.Wilson"));
            facade.updateTraineeTrainers(traineeTrainerListUpdateDto);
            System.out.println("Fetched created trainee: " + created.getFirstName());
        }
    }
}