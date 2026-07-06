package com.epam.gymapp.dto.trainee;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeUpdateDto {
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
}
