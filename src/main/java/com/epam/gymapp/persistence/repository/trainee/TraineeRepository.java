package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.persistence.entity.Trainee;

import java.util.List;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    Trainee delete(Long id);
    void deleteByUserId(Long userId);
    List<Trainee> getAll();
    Trainee getByUserId(Long id);
    Trainee get(Long id);
}
