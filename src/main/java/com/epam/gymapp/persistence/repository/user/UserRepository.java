package com.epam.gymapp.persistence.repository.user;

import com.epam.gymapp.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    boolean existsUserByUsernameAndPassword(String username, String password);

    User getByUsername(String username);

    @Modifying
    @Query("""
        UPDATE User u
        SET u.password = :newPassword
        WHERE u.username = :username
        AND u.password = :oldPassword
    """)
    int updatePasswordByUsername(
            @Param("username") String username,
            @Param("oldPassword") String oldPassword,
            @Param("newPassword") String newPassword
    );

    boolean existsByUsername(String username);

}