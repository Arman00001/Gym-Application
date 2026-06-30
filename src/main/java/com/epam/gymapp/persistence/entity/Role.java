package com.epam.gymapp.persistence.entity;

public enum Role {
    TRAINER,
    TRAINEE;

    public static Role parseRole(String part) {
        if(part.equalsIgnoreCase(TRAINER.name()))
            return TRAINER;
        else return TRAINEE;
    }
}