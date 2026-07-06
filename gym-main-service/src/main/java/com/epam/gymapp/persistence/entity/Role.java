package com.epam.gymapp.persistence.entity;

public enum Role {
    TRAINER,
    TRAINEE,
    ADMIN;

    public static Role parseRole(String part) {
        if (part.equalsIgnoreCase(ADMIN.name())) {
            return ADMIN;
        } else if (part.equalsIgnoreCase(TRAINER.name())) {
            return TRAINER;
        } else {
            return TRAINEE;
        }
    }
}