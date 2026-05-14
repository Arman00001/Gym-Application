package com.epam.gymapp.dto.trainee;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class TraineeCreateDto {
    private String firstName;
    private String lastName;
    private OffsetDateTime dateOfBirth;
    private String address;
}