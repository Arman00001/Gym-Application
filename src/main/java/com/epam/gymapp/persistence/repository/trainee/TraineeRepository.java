package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.persistence.entity.Trainee;

import java.util.List;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    void delete(String username);
    Trainee get(String username);
    List<Trainee> getAll();
}
