package com.epam.gymapp.dto.trainer;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TrainerUpdateDto {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private String specialization;
}
