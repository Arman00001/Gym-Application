package com.epam.gymapp.service.authentication;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.user.LoginResponse;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import com.epam.gymapp.security.CustomUserDetailsService;
import com.epam.gymapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int FAILED_ATTEMPT_WINDOW_MINUTES = 5;
    private static final int BLOCK_DURATION_MINUTES = 5;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Transactional(noRollbackFor = {
            BadCredentialsException.class,
            LockedException.class
    })
    public LoginResponse login(AuthenticationRequestDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Incorrect Credentials"));

        LocalDateTime now = LocalDateTime.now();

        if (user.getBlockedUntil() != null &&
                user.getBlockedUntil().isAfter(now)) {
            throw new LockedException("User is blocked for 5 minutes");
        }

        if (user.getBlockedUntil() != null && !user.getBlockedUntil().isAfter(now)) {
            user.setBlockedUntil(null);
            user.setFailedLoginAttempts(0);
            user.setLastFailedLoginAt(null);
            userRepository.save(user);
        }


        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername(),
                            dto.getPassword()
                    )
            );

            user.setFailedLoginAttempts(0);
            user.setLastFailedLoginAt(null);
            user.setBlockedUntil(null);
            userRepository.save(user);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(dto.getUsername());

            return LoginResponse.builder()
                    .withUsername(dto.getUsername())
                    .withAccessToken(jwtUtil.generateAccessToken(userDetails))
                    .withRefreshToken(jwtUtil.generateRefreshToken(userDetails))
                    .build();

        } catch (AuthenticationException ex) {
            handleLoginFailure(user, now);
            throw new BadCredentialsException("Incorrect Credentials");
        }
    }

    private void handleLoginFailure(User user, LocalDateTime now) {
        LocalDateTime lastFailedLoginAt = user.getLastFailedLoginAt();

        boolean previousAttemptExpired =
                lastFailedLoginAt == null ||
                        lastFailedLoginAt.isBefore(now.minusMinutes(FAILED_ATTEMPT_WINDOW_MINUTES));

        int attempts;

        if (previousAttemptExpired) {
            attempts = 1;
        } else {
            attempts = user.getFailedLoginAttempts() + 1;
        }

        user.setFailedLoginAttempts(attempts);
        user.setLastFailedLoginAt(now);


        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setFailedLoginAttempts(0);
            user.setBlockedUntil(now.plusMinutes(BLOCK_DURATION_MINUTES));
            userRepository.save(user);

            throw new LockedException("User is blocked for 5 minutes");
        } else {
            user.setFailedLoginAttempts(attempts);
        }

        userRepository.save(user);
    }
}
