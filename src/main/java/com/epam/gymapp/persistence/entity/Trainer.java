package com.epam.gymapp.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Trainer {
    private Long id;
    private Long userId;
    private String specialization;
    private TrainingType type;
}