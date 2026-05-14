package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createTraining_shouldSaveTrainingAndReturnDto() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("John.Smith");
        dto.setTrainerUsername("Alex.Brown");
        dto.setName("Morning Yoga");
        dto.setDate(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        dto.setDuration(Duration.ofHours(1));

        Trainee trainee = new Trainee();
        trainee.setUsername("John.Smith");

        Trainer trainer = new Trainer();
        trainer.setUsername("Alex.Brown");

        when(traineeRepository.get("John.Smith")).thenReturn(trainee);
        when(trainerRepository.get("Alex.Brown")).thenReturn(trainer);

        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            training.setId(1L);
            return training;
        });

        TrainingDto result = trainingService.createTraining(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTraineeUsername()).isEqualTo("John.Smith");
        assertThat(result.getTrainerUsername()).isEqualTo("Alex.Brown");
        assertThat(result.getName()).isEqualTo("Morning Yoga");
        assertThat(result.getDuration()).isEqualTo(Duration.ofHours(1));

        verify(traineeRepository).get("John.Smith");
        verify(trainerRepository).get("Alex.Brown");
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void getTraining_shouldReturnDto_whenTrainingExists() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainerUsername("Alex.Brown");
        training.setTraineeUsername("John.Smith");
        training.setName("Morning Yoga");
        training.setDuration(Duration.ofHours(1));

        when(trainingRepository.get(1L)).thenReturn(training);

        TrainingDto result = trainingService.getTraining(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTrainerUsername()).isEqualTo("Alex.Brown");
        assertThat(result.getTraineeUsername()).isEqualTo("John.Smith");
        assertThat(result.getName()).isEqualTo("Morning Yoga");
    }

    @Test
    void getTraining_shouldThrowException_whenTrainingDoesNotExist() {
        when(trainingRepository.get(4L)).thenReturn(null);

        assertThatThrownBy(() -> trainingService.getTraining(4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training does not exist");
    }
}