package com.epam.gymapp.util;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsernameGeneratorTest {

    @Test
    void generate_shouldReturnBaseUsername_whenUsernameDoesNotExist() {
        TraineeRepository traineeRepository = mock(TraineeRepository.class);
        TrainerRepository trainerRepository = mock(TrainerRepository.class);

        when(traineeRepository.getAll()).thenReturn(List.of());
        when(trainerRepository.getAll()).thenReturn(List.of());

        UsernameGenerator generator = new UsernameGenerator(trainerRepository, traineeRepository);
        generator.init();

        String username = generator.generate("John", "Smith");

        assertThat(username).isEqualTo("John.Smith");
    }

    @Test
    void generate_shouldAddSuffix_whenUsernameAlreadyExists() {
        Trainee existing = new Trainee();
        existing.setUsername("John.Smith");

        TraineeRepository traineeRepository = mock(TraineeRepository.class);
        TrainerRepository trainerRepository = mock(TrainerRepository.class);

        when(traineeRepository.getAll()).thenReturn(List.of(existing));
        when(trainerRepository.getAll()).thenReturn(List.of());

        UsernameGenerator generator = new UsernameGenerator(trainerRepository, traineeRepository);
        generator.init();

        String username = generator.generate("John", "Smith");

        assertThat(username).isEqualTo("John.Smith1");
    }

    @Test
    void generate_shouldIncrementSuffixForMultipleDuplicates() {
        Trainee first = new Trainee();
        first.setUsername("John.Smith");

        Trainer second = new Trainer();
        second.setUsername("John.Smith1");

        TraineeRepository traineeRepository = mock(TraineeRepository.class);
        TrainerRepository trainerRepository = mock(TrainerRepository.class);

        when(traineeRepository.getAll()).thenReturn(List.of(first));
        when(trainerRepository.getAll()).thenReturn(List.of(second));

        UsernameGenerator generator = new UsernameGenerator(trainerRepository, traineeRepository);
        generator.init();

        String username = generator.generate("John", "Smith");

        assertThat(username).isEqualTo("John.Smith2");
    }
}