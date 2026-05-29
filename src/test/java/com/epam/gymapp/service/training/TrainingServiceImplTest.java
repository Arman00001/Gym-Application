package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void createTraining_shouldResolveTraineeAndTrainerThenSaveTraining() {
        TrainingType type = trainingType(5L, "Yoga");
        TrainingCreateDto dto = createDto(type);
        Trainee trainee = trainee(1L, user(10L, "John", "Smith", "John.Smith"));
        Trainer trainer = trainer(2L, user(20L, "Alex", "Brown", "Alex.Brown"), type);

        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            training.setId(1L);
            return training;
        });

        TrainingDto result = trainingService.createTraining(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Morning Yoga");
        assertThat(result.getDate()).isEqualTo(LocalDate.parse("2025-01-01"));
        assertThat(result.getDuration()).isEqualTo(10000L);
        verify(trainingRepository).save(argThat(training ->
                training.getTrainee() == trainee
                        && training.getTrainer() == trainer
                        && training.getType() == type
                        && training.getName().equals("Morning Yoga")
                        && training.getDate().equals(LocalDate.parse("2025-01-01"))
                        && training.getDuration().equals(10000L)
        ));
    }

    @Test
    void createTraining_shouldThrowException_whenTraineeProfileDoesNotExist() {
        TrainingCreateDto dto = createDto(trainingType(5L, "Yoga"));
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.createTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");

        verify(trainerRepository, never()).getByUsername(any());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createTraining_shouldThrowException_whenTrainerProfileDoesNotExist() {
        TrainingCreateDto dto = createDto(trainingType(5L, "Yoga"));
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.of(trainee(1L, user(10L, "John", "Smith", "John.Smith"))));
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.createTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void getTraining_shouldReturnDto_whenTrainingExists() {
        TrainingType type = trainingType(5L, "Yoga");
        Training training = new Training();
        training.setId(1L);
        training.setName("Morning Yoga");
        training.setType(type);
        training.setDate(LocalDate.parse("2025-01-01"));
        training.setDuration(10000L);
        when(trainingRepository.get(1L)).thenReturn(Optional.of(training));

        TrainingDto result = trainingService.getTraining(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Morning Yoga");
        assertThat(result.getType()).isSameAs(type);
        assertThat(result.getDate()).isEqualTo(LocalDate.parse("2025-01-01"));
        assertThat(result.getDuration()).isEqualTo(10000L);
        verify(trainingRepository).get(1L);
    }

    @Test
    void getTraining_shouldThrowException_whenTrainingDoesNotExist() {
        when(trainingRepository.get(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.getTraining(4L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training does not exist");

        verify(trainingRepository).get(4L);
    }

    private static TrainingCreateDto createDto(TrainingType type) {
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setTraineeUsername("John.Smith");
        dto.setTrainerUsername("Alex.Brown");
        dto.setName("Morning Yoga");
        dto.setDate(LocalDate.parse("2025-01-01"));
        dto.setType(type);
        dto.setDuration(10000L);
        return dto;
    }

    private static User user(Long id, String firstName, String lastName, String username) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setIsActive(true);
        return user;
    }

    private static Trainee trainee(Long id, User user) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(user);
        return trainee;
    }

    private static Trainer trainer(Long id, User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    private static TrainingType trainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setName(name);
        return trainingType;
    }
}
