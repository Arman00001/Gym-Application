package com.epam.gymapp.persistence.repository.trainer;

import com.epam.gymapp.persistence.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);
    Trainer update(Trainer trainer);
    Optional<Trainer> get(Long id);
    Optional<Trainer> getByUserId(Long userId);
    List<Trainer> getAll();
    Optional<Trainer> getByUsername(String username);
}
