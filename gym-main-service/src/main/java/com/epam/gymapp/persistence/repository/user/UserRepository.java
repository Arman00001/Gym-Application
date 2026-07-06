package com.epam.gymapp.persistence.repository.user;

import com.epam.gymapp.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    User getByUsername(String username);

    boolean existsByUsername(String username);
}