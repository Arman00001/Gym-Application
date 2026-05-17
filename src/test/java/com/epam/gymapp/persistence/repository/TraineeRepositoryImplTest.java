package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TraineeRepositoryImplTest {

    private TraineeRepositoryImpl traineeRepository;

    @BeforeEach
    void setUp() {
        Map<Long, Trainee> storage = new HashMap<>();

        traineeRepository = new TraineeRepositoryImpl();
        traineeRepository.setStorage(storage);
    }

    @Test
    void save_shouldAssignIdAndStoreTraineeById() {
        Trainee trainee = new Trainee();
        trainee.setUserId(10L);
        trainee.setDateOfBirth(OffsetDateTime.parse("2000-01-01T00:00:00Z"));
        trainee.setAddress("New York");

        Trainee saved = traineeRepository.save(trainee);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUserId()).isEqualTo(10L);
        assertThat(traineeRepository.get(1L)).isSameAs(saved);
    }

    @Test
    void getByUserId_shouldReturnTrainee_whenExists() {
        Trainee trainee = new Trainee();
        trainee.setUserId(10L);

        traineeRepository.save(trainee);

        Trainee result = traineeRepository.getByUserId(10L);

        assertThat(result).isSameAs(trainee);
    }

    @Test
    void getByUserId_shouldReturnNull_whenDoesNotExist() {
        Trainee result = traineeRepository.getByUserId(999L);

        assertThat(result).isNull();
    }

    @Test
    void update_shouldReplaceExistingTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUserId(10L);
        trainee.setAddress("Old address");

        Trainee saved = traineeRepository.save(trainee);
        saved.setAddress("New address");

        Trainee updated = traineeRepository.update(saved);

        assertThat(updated.getAddress()).isEqualTo("New address");
        assertThat(traineeRepository.get(saved.getId()).getAddress()).isEqualTo("New address");
    }

    @Test
    void update_shouldThrowException_whenTraineeDoesNotExist() {
        Trainee trainee = new Trainee();
        trainee.setId(999L);
        trainee.setUserId(10L);

        assertThatThrownBy(() -> traineeRepository.update(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
    }

    @Test
    void delete_shouldRemoveTraineeById() {
        Trainee trainee = new Trainee();
        trainee.setUserId(10L);

        Trainee saved = traineeRepository.save(trainee);

        traineeRepository.delete(saved.getId());

        assertThat(traineeRepository.get(saved.getId())).isNull();
    }

    @Test
    void delete_shouldThrowException_whenTraineeDoesNotExist() {
        assertThatThrownBy(() -> traineeRepository.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
    }

    @Test
    void getAll_shouldReturnAllTrainees() {
        Trainee first = new Trainee();
        first.setUserId(10L);

        Trainee second = new Trainee();
        second.setUserId(20L);

        traineeRepository.save(first);
        traineeRepository.save(second);

        assertThat(traineeRepository.getAll()).hasSize(2);
    }
}