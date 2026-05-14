package com.epam.gymapp.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Trainer extends User {
    private Long id;
    private String specialization;
    private TrainingType type;

    public Trainer(
            String firstName,
            String lastName,
            String username,
            String password,
            Boolean isActive,
            Long id,
            Long userId,
            String specialization,
            TrainingType type
    ) {
        super(userId, firstName, lastName, username, password, isActive);
        this.id = id;
        this.specialization = specialization;
        this.type = type;
    }
}