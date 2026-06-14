package com.epam.gymapp.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeCreateDto {
    @NotBlank(message = "first name should not be blank")
    private String firstName;
    @NotBlank(message = "last name should not be blank")
    private String lastName;

    private LocalDate dateOfBirth;
    private String address;
}