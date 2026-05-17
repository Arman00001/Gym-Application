package com.epam.gymapp.persistence.repository.trainer;

import com.epam.gymapp.persistence.entity.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private static final Logger log = LoggerFactory.getLogger(TrainerRepositoryImpl.class);

    private Map<Long, Trainer> storage;
    private Long lastId = 0L;

    @Autowired
    public void setStorage(@Qualifier("trainerStorage") Map<Long, Trainer> storage) {
        this.storage = storage;
    }

    @Override
    public Trainer save(Trainer trainer) {
        Long newId = ++lastId;
        trainer.setId(newId);
        storage.put(newId, trainer);

        log.debug("Saved trainer to storage. id={}", trainer.getId());
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if(!storage.containsKey(trainer.getId())){
            log.warn("Cannot update trainer. Not found in storage. id={}", trainer.getId());
            throw new IllegalArgumentException("Trainer does not exist");
        }
        storage.put(trainer.getId(), trainer);

        log.debug("Updated trainer in storage. id={}", trainer.getId());
        return trainer;
    }

    @Override
    public Trainer get(Long id) {
        log.debug("Getting trainer from storage. id={}", id);
        return storage.get(id);
    }

    @Override
    public Trainer getByUserId(Long userId) {
        return storage.values().stream()
                .filter(trainer -> trainer.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Trainer> getAll() {
        return new ArrayList<>(storage.values());
    }
}
