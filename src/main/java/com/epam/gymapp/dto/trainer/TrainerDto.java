package com.epam.gymapp.dto.trainer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerDto {
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private String specialization;
}