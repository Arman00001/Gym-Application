package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.persistence.entity.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private static final Logger log = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

    private Map<Long, Trainee> storage;
    private Long lastId = 0L;

    @Autowired
    public void setStorage(@Qualifier("traineeStorage") Map<Long, Trainee> storage) {
        this.storage = storage;
    }

    @Override
    public Trainee save(Trainee trainee) {
        Long newId = ++lastId;
        trainee.setId(newId);
        storage.put(trainee.getId(), trainee);

        log.debug("Saved trainee to storage. id={}", trainee.getId());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if(!storage.containsKey(trainee.getId())){
            log.warn("Cannot update trainee. Not found in storage. id={}", trainee.getId());
            throw new IllegalArgumentException("Trainee does not exist");
        }
        storage.put(trainee.getUserId(), trainee);

        log.debug("Updated trainee in storage. user id={}", trainee.getUserId());
        return trainee;
    }

    @Override
    public Trainee delete(Long id) {
        Trainee removed = storage.remove(id);
        if (removed == null) {
            log.warn("Cannot delete trainee. Not found in storage. id={}", id);
            throw new IllegalArgumentException("Trainee does not exist");
        }

        log.debug("Deleted trainee from storage. id={}", id);
        return removed;
    }

    @Override
    public void deleteByUserId(Long userId) {
        storage.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .findFirst().ifPresent(trainee -> storage.remove(trainee.getId()));
    }

    public List<Trainee> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Trainee getByUserId(Long userId) {
        return storage.values().stream()
                .filter(trainee -> trainee.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Trainee get(Long id) {
        return storage.get(id);
    }
}
