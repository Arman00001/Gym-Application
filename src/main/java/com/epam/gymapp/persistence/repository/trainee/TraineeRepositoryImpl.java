package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.persistence.Storage;
import com.epam.gymapp.persistence.entity.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private static final Logger log = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

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

        log.debug("Saved trainee to storage. username={}, id={}", trainee.getUsername(), trainee.getId());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if(!storage.containsKey(trainee.getUsername())){
            log.warn("Cannot update trainee. Not found in storage. username={}", trainee.getUsername());
            throw new IllegalArgumentException("Trainee does not exist");
        }
        storage.put(trainee.getUsername(), trainee);

        log.debug("Updated trainee in storage. username={}", trainee.getUsername());
        return trainee;
    }

    @Override
    public void delete(String username) {
        Trainee removed = storage.remove(username);
        if (removed == null) {
            log.warn("Cannot delete trainee. Not found in storage. username={}", username);
            throw new IllegalArgumentException("Trainee does not exist");
        }

        log.debug("Deleted trainee from storage. username={}", username);
    }

    @Override
    public Trainee get(String username) {
        log.debug("Getting trainee from storage. username={}", username);
        return storage.get(username);
    }

    public List<Trainee> getAll() {
        return new ArrayList<>(storage.values());
    }

}
