package com.epam.gymapp.persistence;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class Storage {
    private final Map<String, Trainee> trainees = new HashMap<>();
    private final Map<String, Trainer> trainers = new HashMap<>();
    private final Map<String, Training> trainings = new HashMap<>();

}