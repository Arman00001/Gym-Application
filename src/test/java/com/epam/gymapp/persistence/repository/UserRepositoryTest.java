package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldPersistUser() {
        User user = user(null, "John", "Smith", "John.Smith");

        User saved = userRepository.saveAndFlush(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("John.Smith");
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        User saved = userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        Optional<User> result = userRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("John.Smith");
    }

    @Test
    void findByUsername_shouldReturnUser_whenExists() {
        userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        Optional<User> result = userRepository.findByUsername("John.Smith");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("John.Smith");
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenDoesNotExist() {
        Optional<User> result = userRepository.findByUsername("missing");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        assertThat(userRepository.existsByUsername("John.Smith")).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        assertThat(userRepository.existsByUsername("missing")).isFalse();
    }

    @Test
    void existsUserByUsernameAndPassword_shouldReturnTrue_whenUsernameAndPasswordMatch() {
        userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        boolean result = userRepository.existsUserByUsernameAndPassword(
                "John.Smith",
                "password12"
        );

        assertThat(result).isTrue();
    }

    @Test
    void existsUserByUsernameAndPassword_shouldReturnFalse_whenPasswordDoesNotMatch() {
        userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        boolean result = userRepository.existsUserByUsernameAndPassword(
                "John.Smith",
                "wrongPassword"
        );

        assertThat(result).isFalse();
    }

    @Test
    void save_shouldUpdateExistingUser() {
        User saved = userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        saved.setFirstName("Johnny");

        User updated = userRepository.saveAndFlush(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getFirstName()).isEqualTo("Johnny");
    }

    @Test
    void updatePasswordByUsername_shouldChangePassword_whenOldPasswordMatches() {
        userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        int updatedRows = userRepository.updatePasswordByUsername(
                "John.Smith",
                "password12",
                "newPassword12"
        );

        assertThat(updatedRows).isEqualTo(1);
        assertThat(
                userRepository.existsUserByUsernameAndPassword("John.Smith", "newPassword12")
        ).isTrue();
    }

    @Test
    void updatePasswordByUsername_shouldNotChangePassword_whenOldPasswordDoesNotMatch() {
        userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        int updatedRows = userRepository.updatePasswordByUsername(
                "John.Smith",
                "wrongPassword",
                "newPassword12"
        );

        assertThat(updatedRows).isZero();
        assertThat(
                userRepository.existsUserByUsernameAndPassword("John.Smith", "password12")
        ).isTrue();
    }

    @Test
    void delete_shouldRemoveUser() {
        User saved = userRepository.saveAndFlush(
                user(null, "John", "Smith", "John.Smith")
        );

        userRepository.delete(saved);
        userRepository.flush();

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    private static User user(Long id, String firstName, String lastName, String username) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("password12");
        user.setIsActive(true);
        return user;
    }
}