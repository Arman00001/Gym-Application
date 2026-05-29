package com.epam.gymapp.dto.trainee;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TraineeDto {
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;

    private List<TraineeTrainerDto> trainers;
}