package com.epam.gymapp.security;

import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom Spring Security user details service.
 *
 * <p>
 * Loads application users by username and converts them into Spring Security
 * {@link UserDetails} objects used during authentication.
 * </p>
 *
 * <p>
 * Inactive users are marked as disabled, and the application role is converted
 * into a Spring Security authority.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Incorrect Credentials"));


        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.getIsActive())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
