package com.epam.gymapp.persistence.repository.training;

import com.epam.gymapp.persistence.Storage;
import com.epam.gymapp.persistence.entity.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private static final Logger log = LoggerFactory.getLogger(TrainingRepositoryImpl.class);

    private Map<Long, Training> storage;
    private Long lastId = 0L;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage.getTrainings();
    }

    @Override
    public Training save(Training training) {
        Long newId = ++lastId;
        training.setId(newId);
        storage.put(newId, training);

        log.debug("Saved training to storage. Trainer username={}, id={}",
                training.getTrainerUsername(),
                training.getId());

        return training;
    }

    @Override
    public Training get(Long id) {
        log.debug("Getting training from storage. Training id={}", id);
        return storage.get(id);
    }
}
