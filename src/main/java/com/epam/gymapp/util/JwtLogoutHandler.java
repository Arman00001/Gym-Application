package com.epam.gymapp.util;

import com.epam.gymapp.persistence.entity.BlacklistedToken;
import com.epam.gymapp.persistence.repository.token.BlacklistedTokenRepository;
import com.epam.gymapp.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtUtil.AUTH_TYPE)) {
            return;
        }

        String token = authorizationHeader.substring(JwtUtil.AUTH_TYPE.length());

        if (!jwtUtil.isVerified(token)) {
            return;
        }

        if (blacklistedTokenRepository.existsByToken(token)) {
            return;
        }

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiresAt(
                jwtUtil.getExpirationDate(token)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        blacklistedTokenRepository.save(blacklistedToken);
    }
}

