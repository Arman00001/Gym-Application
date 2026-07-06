package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldPersistTrainee() {
        User savedUser = userRepository.saveAndFlush(user(null, "John.Smith"));

        Trainee trainee = trainee(null, savedUser);

        Trainee saved = traineeRepository.saveAndFlush(trainee);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void getByUsername_shouldReturnTrainee_whenExists() {
        User savedUser = userRepository.saveAndFlush(user(null, "John.Smith"));
        Trainee savedTrainee = traineeRepository.saveAndFlush(trainee(null, savedUser));

        Optional<Trainee> result = traineeRepository.getByUsername("John.Smith");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedTrainee.getId());
        assertThat(result.get().getUser().getUsername()).isEqualTo("John.Smith");
    }

    @Test
    void getByUsername_shouldReturnEmptyOptional_whenDoesNotExist() {
        Optional<Trainee> result = traineeRepository.getByUsername("missing");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnTrainee_whenExists() {
        User savedUser = userRepository.saveAndFlush(user(null, "John.Smith"));
        Trainee savedTrainee = traineeRepository.saveAndFlush(trainee(null, savedUser));

        Optional<Trainee> result = traineeRepository.findById(savedTrainee.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedTrainee.getId());
    }

    @Test
    void save_shouldUpdateExistingTrainee() {
        User savedUser = userRepository.saveAndFlush(user(null, "John.Smith"));
        Trainee savedTrainee = traineeRepository.saveAndFlush(trainee(null, savedUser));

        savedTrainee.setAddress("New address");

        Trainee updated = traineeRepository.saveAndFlush(savedTrainee);

        assertThat(updated.getId()).isEqualTo(savedTrainee.getId());
        assertThat(updated.getAddress()).isEqualTo("New address");
    }

    @Test
    void delete_shouldRemoveTrainee() {
        User savedUser = userRepository.saveAndFlush(user(null, "John.Smith"));
        Trainee savedTrainee = traineeRepository.saveAndFlush(trainee(null, savedUser));

        traineeRepository.delete(savedTrainee);
        traineeRepository.flush();

        assertThat(traineeRepository.findById(savedTrainee.getId())).isEmpty();
    }

    @Test
    void deleteByUsername_shouldRemoveTrainee() {
        User savedUser = userRepository.saveAndFlush(user(null, "John.Smith"));
        Trainee savedTrainee = traineeRepository.saveAndFlush(trainee(null, savedUser));

        traineeRepository.deleteByUsername("John.Smith");
        traineeRepository.flush();

        assertThat(traineeRepository.findById(savedTrainee.getId())).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllTrainees() {
        User user1 = userRepository.saveAndFlush(user(null, "John.Smith"));
        User user2 = userRepository.saveAndFlush(user(null, "Jane.Smith"));

        Trainee trainee1 = traineeRepository.saveAndFlush(trainee(null, user1));
        Trainee trainee2 = traineeRepository.saveAndFlush(trainee(null, user2));

        List<Trainee> result = traineeRepository.findAll();

        assertThat(result)
                .extracting(Trainee::getId)
                .contains(trainee1.getId(), trainee2.getId());
    }

    private static Trainee trainee(Long id, User user) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.parse("2000-01-01"));
        trainee.setAddress("New York");
        return trainee;
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername(username);
        user.setPassword("password12");
        user.setIsActive(true);
        return user;
    }
}