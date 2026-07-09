package com.epam.gymapp.service.authentication;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.user.LoginResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;

/**
 * Service interface for user authentication.
 *
 * <p>
 * Defines the operation for authenticating users and issuing JWT tokens.
 * </p>
 */
public interface AuthenticationService {

    /**
     * Authenticates a user using the provided credentials.
     *
     * @param dto the authentication request containing username and password
     * @return the login response containing the username, access token, and refresh token
     * @throws BadCredentialsException if the credentials are incorrect
     * @throws LockedException if the user is temporarily blocked due to failed login attempts
     */
    LoginResponse login(AuthenticationRequestDto dto);
}