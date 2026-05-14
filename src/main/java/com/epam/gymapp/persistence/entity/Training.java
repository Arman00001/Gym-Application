package com.epam.gymapp.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Training {
    private Long id;
    private String trainerUsername;
    private String traineeUsername;
    private String name;
    private TrainingType type;
    private OffsetDateTime date;
    private Duration duration;
}
