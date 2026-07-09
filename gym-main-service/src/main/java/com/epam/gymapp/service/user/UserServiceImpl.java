package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.dto.user.CreatedUserResult;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.Role;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import com.epam.gymapp.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link UserService}.
 *
 * <p>
 * This implementation handles user creation, retrieval, profile updates, and password
 * changes. During user creation a unique username is generated,
 * as well as a random password. The password is then encoded. The user is assigned
 * the provided role, is marked active and saved to the database.
 * </p>
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a user with a generated unique username and initial raw password.
     *
     * <p>
     * The raw password is returned only in the creation result, while the encoded
     * password is stored in the database.
     * </p>
     */
    @Override
    public CreatedUserResult createUser(UserCreateDto dto, Role role) {
        log.info("Creating user profile for {} {}", dto.getFirstName(), dto.getLastName());
        User user = UserMapper.INSTANCE.userCreateToUser(dto);

        user.setUsername(generateUsername(user.getFirstName(), user.getLastName()));

        String rawPassword = passwordGenerator.generate();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        user.setPassword(hashedPassword);
        user.setIsActive(true);
        user.setRole(role);

        User result = userRepository.save(user);
        log.info("User with username={} created", result.getUsername());

        return new CreatedUserResult(result, rawPassword);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist"));
    }

    @Override
    @Transactional
    public User updateUser(UserUpdateDto dto) {
        log.info("Updating user {}", dto.getUsername());
        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> {
            log.warn("User not found. username={}", dto.getUsername());
            return new ResourceNotFoundException("User does not exist");
        });

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsActive(dto.getIsActive());

        return userRepository.save(user);
    }

    /**
     * Validates the old password before encoding and saving the new password.
     *
     * @throws BadCredentialsException if the user does not exist or the old password is incorrect
     */
    @Override
    public void changePassword(String username, ChangePasswordRequestDto dto) {
        log.info("Changing password for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("No user found with username {}", username);
                    return new BadCredentialsException("Incorrect Credentials");
                });

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect Credentials");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", username);
    }

    /**
     * Generate a unique username based on the user's first and last name.
     *
     * <p>
     * The initial username is created in the form {@code firstName.lastName}.
     * If that username already exists, a numeric suffix is appended and incremented
     * until a unique username is found.
     * </p>
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @return a unique username
     */
    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;

        if (!userRepository.existsByUsername(baseUsername)) {
            return baseUsername;
        }

        int suffix = 1;
        while (userRepository.existsByUsername(baseUsername + suffix)) {
            suffix++;
        }

        return baseUsername + suffix;
    }
}
