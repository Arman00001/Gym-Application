package com.epam.gymapp.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerCreateDto {
    @NotBlank(message = "first name should not be blank")
    private String firstName;
    @NotBlank(message = "last name should not be blank")
    private String lastName;
    @NotBlank(message = "specialization should not be blank")
    private String specialization;
}