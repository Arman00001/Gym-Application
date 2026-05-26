package com.epam.gymapp.persistence.repository.trainingtype;

import com.epam.gymapp.persistence.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    TrainingType save(TrainingType trainingType);
    TrainingType update(TrainingType trainingType);
    void delete(Long id);
    void delete(TrainingType trainingType);
    Optional<TrainingType> get(Long id);
    Optional<TrainingType> getByName(String name);
    List<TrainingType> getAll();
}
