package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainerRepositoryImplTest {

    private TrainerRepositoryImpl trainerRepository;

    @BeforeEach
    void setUp() {
        Map<Long, Trainer> storage = new HashMap<>();

        trainerRepository = new TrainerRepositoryImpl();
        trainerRepository.setStorage(storage);
    }

    @Test
    void save_shouldAssignIdAndStoreTrainerById() {
        Trainer trainer = new Trainer();
        trainer.setUserId(10L);
        trainer.setSpecialization("Yoga");

        TrainingType type = new TrainingType();
        type.setName("Yoga");
        trainer.setType(type);

        Trainer saved = trainerRepository.save(trainer);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUserId()).isEqualTo(10L);
        assertThat(trainerRepository.get(1L)).isSameAs(saved);
    }

    @Test
    void getByUserId_shouldReturnTrainer_whenExists() {
        Trainer trainer = new Trainer();
        trainer.setUserId(10L);

        trainerRepository.save(trainer);

        Trainer result = trainerRepository.getByUserId(10L);

        assertThat(result).isSameAs(trainer);
    }

    @Test
    void getByUserId_shouldReturnNull_whenDoesNotExist() {
        Trainer result = trainerRepository.getByUserId(999L);

        assertThat(result).isNull();
    }

    @Test
    void update_shouldReplaceExistingTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUserId(10L);
        trainer.setSpecialization("Yoga");

        Trainer saved = trainerRepository.save(trainer);
        saved.setSpecialization("Fitness");

        Trainer updated = trainerRepository.update(saved);

        assertThat(updated.getSpecialization()).isEqualTo("Fitness");
        assertThat(trainerRepository.get(saved.getId()).getSpecialization()).isEqualTo("Fitness");
    }

    @Test
    void update_shouldThrowException_whenTrainerDoesNotExist() {
        Trainer trainer = new Trainer();
        trainer.setId(999L);
        trainer.setUserId(10L);

        assertThatThrownBy(() -> trainerRepository.update(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");
    }

    @Test
    void getAll_shouldReturnAllTrainers() {
        Trainer first = new Trainer();
        first.setUserId(10L);

        Trainer second = new Trainer();
        second.setUserId(20L);

        trainerRepository.save(first);
        trainerRepository.save(second);

        assertThat(trainerRepository.getAll()).hasSize(2);
    }
}