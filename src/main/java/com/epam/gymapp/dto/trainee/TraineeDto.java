package com.epam.gymapp.dto.trainee;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class TraineeDto {
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private OffsetDateTime dateOfBirth;
    private String address;
}