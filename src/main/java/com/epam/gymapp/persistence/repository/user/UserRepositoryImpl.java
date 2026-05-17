package com.epam.gymapp.persistence.repository.user;

import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

    private Map<Long, User> storage;
    private Long lastId = 0L;

    @Autowired
    public void setStorage(@Qualifier("userStorage") Map<Long, User> users) {
        this.storage = users;
    }

    @Override
    public User save(User user) {
        Long id = ++lastId;
        user.setId(id);
        storage.put(id, user);
        return user;
    }

    @Override
    public User getById(Long id) {
        return storage.get(id);
    }

    @Override
    public User getByUsername(String username) {
        return storage.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return storage.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public User update(User user) {
        if(!storage.containsKey(user.getId())){
            log.warn("Cannot update user. Not found in storage. id={}", user.getId());
            throw new IllegalArgumentException("User does not exist");
        }

        storage.put(user.getId(), user);
        log.debug("Updated user in storage. user id={}", user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        User removed = storage.remove(id);
        if (removed == null) {
            log.warn("Cannot delete user. Not found in storage. id={}", id);
            throw new IllegalArgumentException("User does not exist");
        }

        log.debug("Deleted user from storage. id={}", id);
    }
}
