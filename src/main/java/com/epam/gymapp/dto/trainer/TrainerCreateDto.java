package com.epam.gymapp.dto.trainer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerCreateDto {
    private String firstName;
    private String lastName;
    private String specialization;
}