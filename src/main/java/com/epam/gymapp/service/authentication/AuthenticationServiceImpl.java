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
public class AuthenticationServiceImpl implements AuthenticationService{

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(AuthenticationRequestDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Incorrect Credentials"));

        if (user.getBlockedUntil() != null &&
                user.getBlockedUntil().isAfter(LocalDateTime.now())) {
            throw new LockedException("User is blocked for 5 minutes");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername(),
                            dto.getPassword()
                    )
            );

            user.setFailedLoginAttempts(0);
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
            int attempts = user.getFailedLoginAttempts() + 1;

            if (attempts >= 3) {
                user.setFailedLoginAttempts(0);
                user.setBlockedUntil(LocalDateTime.now().plusMinutes(5));
            } else {
                user.setFailedLoginAttempts(attempts);
            }

            userRepository.save(user);

            throw new BadCredentialsException("Incorrect Credentials");
        }
    }
}
