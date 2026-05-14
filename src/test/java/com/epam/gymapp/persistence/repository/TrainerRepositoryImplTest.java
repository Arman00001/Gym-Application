package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.Storage;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainerRepositoryImplTest {

    private TrainerRepositoryImpl trainerRepository;

    @BeforeEach
    void setUp() {
        Storage storage = new Storage();
        trainerRepository = new TrainerRepositoryImpl();
        trainerRepository.setStorage(storage);
    }

    @Test
    void save_shouldAssignIdUserIdAndActiveStatus() {
        Trainer trainer = new Trainer();
        trainer.setUsername("Alex.Brown");
        trainer.setFirstName("Alex");
        trainer.setLastName("Brown");

        Trainer saved = trainerRepository.save(trainer);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getIsActive()).isTrue();
        assertThat(trainerRepository.get("Alex.Brown")).isSameAs(saved);
    }

    @Test
    void update_shouldReplaceExistingTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("Alex.Brown");
        trainer.setSpecialization("Yoga");
        trainerRepository.save(trainer);

        trainer.setSpecialization("Fitness");

        Trainer updated = trainerRepository.update(trainer);

        assertThat(updated.getSpecialization()).isEqualTo("Fitness");
    }

    @Test
    void update_shouldThrowException_whenTrainerDoesNotExist() {
        Trainer trainer = new Trainer();
        trainer.setUsername("missing");

        assertThatThrownBy(() -> trainerRepository.update(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");
    }

    @Test
    void getAll_shouldReturnAllTrainers() {
        Trainer first = new Trainer();
        first.setUsername("Alex.Brown");

        Trainer second = new Trainer();
        second.setUsername("Bob.Green");

        trainerRepository.save(first);
        trainerRepository.save(second);

        assertThat(trainerRepository.getAll()).hasSize(2);
    }
}