package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import com.epam.gymapp.service.trainee.TraineeService;
import com.epam.gymapp.service.trainer.TrainerService;
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
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createTraining_shouldCheckTraineeAndTrainerThenSaveTraining() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("John.Smith");
        dto.setTrainerUsername("Alex.Brown");
        dto.setName("Morning Yoga");
        dto.setDate(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        dto.setDuration(Duration.ofHours(1));

        TraineeDto traineeDto = new TraineeDto();

        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUsername("Alex.Brown");

        when(traineeService.getTraineeByUsername("John.Smith")).thenReturn(traineeDto);
        when(trainerService.getTrainerByUsername("Alex.Brown")).thenReturn(trainerDto);

        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            training.setId(1L);
            return training;
        });

        TrainingDto result = trainingService.createTraining(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Morning Yoga");
        assertThat(result.getDate()).isEqualTo(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        assertThat(result.getDuration()).isEqualTo(Duration.ofHours(1));

        verify(traineeService).getTraineeByUsername("John.Smith");
        verify(trainerService).getTrainerByUsername("Alex.Brown");

        verify(trainingRepository).save(argThat(training ->
                training.getName().equals("Morning Yoga")
                        && training.getDate().equals(OffsetDateTime.parse("2025-01-01T10:00:00Z"))
                        && training.getDuration().equals(Duration.ofHours(1))
        ));
    }

    @Test
    void createTraining_shouldThrowException_whenTraineeProfileDoesNotExist() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("John.Smith");
        dto.setTrainerUsername("Alex.Brown");

        when(traineeService.getTraineeByUsername("John.Smith")).thenReturn(null);

        assertThatThrownBy(() -> trainingService.createTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");

        verify(traineeService).getTraineeByUsername("John.Smith");
        verify(trainerService, never()).getTrainerByUsername(any());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_shouldThrowException_whenTrainerProfileDoesNotExist() {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("John.Smith");
        dto.setTrainerUsername("Alex.Brown");

        TraineeDto traineeDto = new TraineeDto();

        when(traineeService.getTraineeByUsername("John.Smith")).thenReturn(traineeDto);
        when(trainerService.getTrainerByUsername("Alex.Brown")).thenReturn(null);

        assertThatThrownBy(() -> trainingService.createTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");

        verify(traineeService).getTraineeByUsername("John.Smith");
        verify(trainerService).getTrainerByUsername("Alex.Brown");
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void getTraining_shouldReturnDto_whenTrainingExists() {
        Training training = new Training();
        training.setId(1L);
        training.setName("Morning Yoga");
        training.setDate(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        training.setDuration(Duration.ofHours(1));

        when(trainingRepository.get(1L)).thenReturn(training);

        TrainingDto result = trainingService.getTraining(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Morning Yoga");
        assertThat(result.getDate()).isEqualTo(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        assertThat(result.getDuration()).isEqualTo(Duration.ofHours(1));

        verify(trainingRepository).get(1L);
    }

    @Test
    void getTraining_shouldThrowException_whenTrainingDoesNotExist() {
        when(trainingRepository.get(4L)).thenReturn(null);

        assertThatThrownBy(() -> trainingService.getTraining(4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training does not exist");

        verify(trainingRepository).get(4L);
    }
}