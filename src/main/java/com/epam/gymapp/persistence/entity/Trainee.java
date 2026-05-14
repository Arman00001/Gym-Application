package com.epam.gymapp.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Trainee extends User {
    private Long id;
    private OffsetDateTime dateOfBirth;
    private String address;

}