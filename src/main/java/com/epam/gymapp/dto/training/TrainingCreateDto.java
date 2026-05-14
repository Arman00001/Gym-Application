package com.epam.gymapp.dto.training;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.OffsetDateTime;

@Getter
@Setter
public class TrainingCreateDto {
    private String traineeUsername;
    private String trainerUsername;
    private String name;
    private OffsetDateTime date;
    private String type;
    private Duration duration;
}