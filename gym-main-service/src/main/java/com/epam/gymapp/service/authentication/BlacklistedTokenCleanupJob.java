package com.epam.gymapp.service.authentication;

import com.epam.gymapp.persistence.repository.token.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.epam.gymapp.security.JwtUtil.JWT_ACCESS_TOKEN_VALIDITY;

/**
 * Scheduled job responsible for removing expired blacklisted JWT tokens.
 *
 * <p>
 * Tokens are kept in the blacklist until their expiration time so they cannot
 * be reused after logout. Once they expire, this job removes them from storage.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class BlacklistedTokenCleanupJob {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Deletes blacklisted tokens whose expiration time is before the current time.
     *
     * <p>
     * This cleanup runs once per hour.
     * </p>
     */
    @Scheduled(fixedRate = JWT_ACCESS_TOKEN_VALIDITY)
    @Transactional
    public void deleteExpiredTokens() {
        blacklistedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}