package com.epam.gymapp.persistence.repository.trainer;

import com.epam.gymapp.persistence.entity.Trainer;

import java.util.List;

public interface TrainerRepository {
    Trainer save(Trainer trainer);
    Trainer update(Trainer trainer);
    Trainer get(String username);
    List<Trainer> getAll();
}
