package com.epam.gymapp.service.authentication;

import com.epam.gymapp.persistence.repository.token.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BlacklistedTokenCleanupJob {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Scheduled(fixedRate = 1000 * 60 * 60)
    @Transactional
    public void deleteExpiredTokens() {
        blacklistedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}