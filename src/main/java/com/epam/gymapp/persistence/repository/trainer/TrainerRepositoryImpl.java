package com.epam.gymapp.persistence.repository.trainer;

import com.epam.gymapp.persistence.Storage;
import com.epam.gymapp.persistence.entity.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private Map<String, Trainer> storage;
    private Long lastId = 0L;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage.getTrainers();
    }

    @Override
    public Trainer save(Trainer trainer) {
        Long newId = ++lastId;
        trainer.setId(newId);
        trainer.setUserId(newId);
        trainer.setIsActive(true);
        storage.put(trainer.getUsername(), trainer);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if(!storage.containsKey(trainer.getUsername())){
            throw new IllegalArgumentException("Trainer does not exist");
        }
        storage.put(trainer.getUsername(), trainer);
        return trainer;
    }

    @Override
    public Trainer get(String username) {
        return storage.get(username);
    }

    @Override
    public List<Trainer> getAll() {
        return new ArrayList<>(storage.values());
    }
}
