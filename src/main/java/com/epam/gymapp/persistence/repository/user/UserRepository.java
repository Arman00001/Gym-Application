package com.epam.gymapp.persistence.repository.user;

import com.epam.gymapp.persistence.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    User update(User user);
    void delete(Long id);
    void deleteUser(User user);
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    boolean existsByUsername(String username);
}