package com.epam.gymapp.persistence.repository.training;

import com.epam.gymapp.persistence.Storage;
import com.epam.gymapp.persistence.entity.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private Map<String, Training> storage;
    private Long lastId = 0L;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage.getTrainings();
    }

    @Override
    public Training save(Training training) {
        Long newId = ++lastId;
        training.setId(newId);
        storage.put(training.getTrainerUsername(), training);
        return training;
    }

    @Override
    public Training get(String trainerUsername) {
        return storage.get(trainerUsername);
    }
}
