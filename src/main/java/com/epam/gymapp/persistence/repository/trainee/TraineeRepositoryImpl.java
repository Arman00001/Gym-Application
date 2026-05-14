package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.persistence.Storage;
import com.epam.gymapp.persistence.entity.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private Map<String, Trainee> storage;
    private Long lastId = 0L;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage.getTrainees();
    }

    @Override
    public Trainee save(Trainee trainee) {
        Long newId = ++lastId;
        trainee.setId(newId);
        trainee.setUserId(newId);
        trainee.setIsActive(true);
        storage.put(trainee.getUsername(), trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if(!storage.containsKey(trainee.getUsername())){
            throw new IllegalArgumentException("Trainee does not exist");
        }
        storage.put(trainee.getUsername(), trainee);
        return trainee;
    }

    @Override
    public void delete(String username) {
        storage.remove(username);
    }

    @Override
    public Trainee get(String username) {
        return storage.get(username);
    }

    public List<Trainee> getAll() {
        return new ArrayList<>(storage.values());
    }

}
