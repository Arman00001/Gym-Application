package com.epam.gymapp.dto.training;

import com.epam.gymapp.persistence.entity.TrainingType;
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
    private TrainingType type;
    private Duration duration;
}