package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.persistence.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    Trainee delete(Long id);
    void deleteByUserId(Long userId);
    void deleteByUsername(String username);
    List<Trainee> getAll();
    Optional<Trainee> getByUserId(Long id);
    Optional<Trainee> get(Long id);
    Optional<Trainee> getByUsername(String username);

}
