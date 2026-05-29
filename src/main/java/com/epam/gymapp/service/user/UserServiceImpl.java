package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import com.epam.gymapp.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public User createUser(UserCreateDto dto) {
        log.info("Creating user profile for {} {}", dto.getFirstName(), dto.getLastName());
        User user = UserMapper.INSTANCE.userCreateToUser(dto);

        user.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        user.setPassword(passwordGenerator.generate());
        user.setIsActive(true);

        User result = userRepository.save(user);
        log.info("User with username={} created", result.getUsername());

        return result;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));
    }

    @Override
    public User updateUser(UserUpdateDto dto) {
        User user = userRepository.getById(dto.getId()).orElseThrow(() -> {
            log.warn("User not found. username={}", dto.getUsername());
            return new IllegalArgumentException("User does not exist");
        });

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsActive(dto.getIsActive());

        return userRepository.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user profile by id. id={}", id);
        userRepository.delete(id);
        log.info("User profile deleted. id={}", id);
    }

    @Override
    public void deleteUser(User user) {
        log.info("Deleting user profile. id={}", user.getId());
        userRepository.deleteUser(user);
        log.info("User profile deleted. id={}", user.getId());
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public boolean isAuthenticated(String username, String password) {
        return userRepository.isAuthenticated(username, password);
    }

    @Override
    public void changePassword(ChangePasswordRequestDto dto) {
        if(isAuthenticated(dto.getUsername(), dto.getOldPassword())){
            userRepository.changePassword(dto.getUsername(),dto.getNewPassword());
        }

        throw new IllegalArgumentException("Incorrect Credentials");
    }

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
