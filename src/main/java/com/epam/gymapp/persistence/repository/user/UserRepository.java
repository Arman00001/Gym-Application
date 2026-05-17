package com.epam.gymapp.persistence.repository.user;

import com.epam.gymapp.persistence.entity.User;

public interface UserRepository {
    User save(User user);
    User getById(Long id);
    User getByUsername(String username);
    boolean existsByUsername(String username);
    User update(User user);
    void delete(Long id);
}