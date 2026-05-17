package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        Map<Long, User> storage = new HashMap<>();

        userRepository = new UserRepositoryImpl();
        userRepository.setStorage(storage);
    }

    @Test
    void save_shouldAssignIdAndStoreUserById() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("John.Smith");
        user.setPassword("password12");
        user.setIsActive(true);

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(userRepository.getById(1L)).isSameAs(saved);
    }

    @Test
    void getByUsername_shouldReturnUser_whenExists() {
        User user = new User();
        user.setUsername("John.Smith");

        userRepository.save(user);

        User result = userRepository.getByUsername("John.Smith");

        assertThat(result).isSameAs(user);
    }

    @Test
    void getByUsername_shouldReturnNull_whenDoesNotExist() {
        User result = userRepository.getByUsername("missing");

        assertThat(result).isNull();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        User user = new User();
        user.setUsername("John.Smith");

        userRepository.save(user);

        boolean result = userRepository.existsByUsername("John.Smith");

        assertThat(result).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        boolean result = userRepository.existsByUsername("missing");

        assertThat(result).isFalse();
    }

    @Test
    void update_shouldReplaceExistingUser() {
        User user = new User();
        user.setFirstName("John");
        user.setUsername("John.Smith");

        User saved = userRepository.save(user);
        saved.setFirstName("Johnny");

        User updated = userRepository.update(saved);

        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(userRepository.getById(saved.getId()).getFirstName()).isEqualTo("Johnny");
    }

    @Test
    void update_shouldThrowException_whenUserDoesNotExist() {
        User user = new User();
        user.setId(999L);

        assertThatThrownBy(() -> userRepository.update(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User does not exist");
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = new User();
        user.setUsername("John.Smith");

        User saved = userRepository.save(user);

        userRepository.delete(saved.getId());

        assertThat(userRepository.getById(saved.getId())).isNull();
    }
}