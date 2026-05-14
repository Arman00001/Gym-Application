package com.epam.gymapp.dto.training;

import com.epam.gymapp.persistence.entity.TrainingType;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.OffsetDateTime;

@Getter
@Setter
public class TrainingDto {
    private Long id;
    private Long userId;
    private String trainerUsername;
    private String traineeUsername;
    private String name;
    private TrainingType type;
    private OffsetDateTime date;
    private Duration duration;
}