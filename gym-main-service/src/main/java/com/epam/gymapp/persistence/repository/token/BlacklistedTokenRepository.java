package com.epam.gymapp.persistence.repository.token;

import com.epam.gymapp.persistence.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository for accessing and managing {@link BlacklistedToken} entities.
 *
 * <p>
 * Used to check whether JWT tokens have been blacklisted and to remove expired
 * blacklisted tokens from storage.
 * </p>
 */
@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}