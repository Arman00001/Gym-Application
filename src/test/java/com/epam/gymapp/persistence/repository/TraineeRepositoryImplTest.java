package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.Storage;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TraineeRepositoryImplTest {

    private TraineeRepositoryImpl traineeRepository;

    @BeforeEach
    void setUp() {
        Storage storage = new Storage();
        traineeRepository = new TraineeRepositoryImpl();
        traineeRepository.setStorage(storage);
    }

    @Test
    void save_shouldAssignIdUserIdAndActiveStatus() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Smith");
        trainee.setFirstName("John");
        trainee.setLastName("Smith");

        Trainee saved = traineeRepository.save(trainee);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getIsActive()).isTrue();
        assertThat(traineeRepository.get("John.Smith")).isSameAs(saved);
    }

    @Test
    void update_shouldReplaceExistingTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Smith");
        trainee.setFirstName("John");
        traineeRepository.save(trainee);

        trainee.setFirstName("Johnny");

        Trainee updated = traineeRepository.update(trainee);

        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(traineeRepository.get("John.Smith").getFirstName()).isEqualTo("Johnny");
    }

    @Test
    void update_shouldThrowException_whenTraineeDoesNotExist() {
        Trainee trainee = new Trainee();
        trainee.setUsername("missing");

        assertThatThrownBy(() -> traineeRepository.update(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
    }

    @Test
    void delete_shouldRemoveTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Smith");
        traineeRepository.save(trainee);

        traineeRepository.delete("John.Smith");

        assertThat(traineeRepository.get("John.Smith")).isNull();
    }

    @Test
    void delete_shouldThrowException_whenTraineeDoesNotExist() {
        assertThatThrownBy(() -> traineeRepository.delete("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
    }

    @Test
    void getAll_shouldReturnAllTrainees() {
        Trainee first = new Trainee();
        first.setUsername("John.Smith");

        Trainee second = new Trainee();
        second.setUsername("Jane.Smith");

        traineeRepository.save(first);
        traineeRepository.save(second);

        assertThat(traineeRepository.getAll()).hasSize(2);
    }
}