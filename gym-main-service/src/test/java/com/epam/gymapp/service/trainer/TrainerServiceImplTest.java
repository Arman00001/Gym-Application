package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.trainer.*;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.dto.user.CreatedUserResult;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.persistence.entity.*;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import com.epam.gymapp.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createTrainer_shouldCreateUserResolveSpecializationAndSaveTrainerProfile() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setSpecialization("Yoga");

        TrainingType yoga = trainingType(5L, "Yoga");
        User user = user(10L, "Alex", "Brown", "Alex.Brown", "password12", true);
        CreatedUserResult createdUser = new CreatedUserResult(user, user.getPassword());
        when(trainingTypeRepository.findByName("Yoga")).thenReturn(Optional.of(yoga));
        when(userService.createUser(any(UserCreateDto.class), eq(Role.TRAINER))).thenReturn(createdUser);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            trainer.setId(1L);
            return trainer;
        });

        TrainerCreateResponse response = trainerService.createTrainer(dto);

        assertThat(response.getUsername()).isEqualTo("Alex.Brown");
        assertThat(response.getPassword()).isEqualTo("password12");

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(trainerCaptor.capture());
        Trainer savedTrainer = trainerCaptor.getValue();
        assertThat(savedTrainer.getUser()).isSameAs(user);
        assertThat(savedTrainer.getSpecialization()).isSameAs(yoga);
        verify(trainingTypeRepository).findByName("Yoga");
        verify(userService).createUser(any(UserCreateDto.class), eq(Role.TRAINER));
    }

    @Test
    void createTrainer_shouldThrowException_whenSpecializationDoesNotExist() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setSpecialization("Missing");
        when(trainingTypeRepository.findByName("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.createTrainer(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Specialization not found");

        verifyNoInteractions(userService, trainerRepository);
    }

    @Test
    void getTrainerByUsername_shouldReturnDto_whenTrainerExists() {
        User user = user(10L, "Alex", "Brown", "Alex.Brown", "password12", true);
        TrainingType yoga = trainingType(5L, "Yoga");
        Trainer trainer = trainer(1L, user, yoga);
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.of(trainer));

        TrainerDto result = trainerService.getTrainerByUsername("Alex.Brown");

        assertThat(result.getUsername()).isEqualTo("Alex.Brown");
        assertThat(result.getFirstName()).isEqualTo("Alex");
        assertThat(result.getLastName()).isEqualTo("Brown");
        assertThat(result.getSpecialization()).isEqualTo("Yoga");
        assertThat(result.getIsActive()).isTrue();
        verify(trainerRepository).getByUsername("Alex.Brown");
        verifyNoInteractions(trainingTypeRepository);
    }

    @Test
    void getTrainerByUsername_shouldThrowException_whenTrainerProfileDoesNotExist() {
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerByUsername("Alex.Brown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Trainer does not exist");
    }

    @Test
    void updateTrainer_shouldUpdateUserFieldsAndSpecialization() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        String username = "Alex.Brown";
        dto.setFirstName("Alexander");
        dto.setLastName("Brown");
        dto.setIsActive(false);
        dto.setSpecialization("Fitness");

        User user = user(10L, "Alex", "Brown", "Alex.Brown", "password12", true);
        TrainingType oldType = trainingType(5L, "Yoga");
        TrainingType newType = trainingType(6L, "Fitness");
        Trainer existingTrainer = trainer(1L, user, oldType);

        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.of(existingTrainer));
        when(trainingTypeRepository.findByName("Fitness")).thenReturn(Optional.of(newType));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDto result = trainerService.updateTrainer(username, dto);

        assertThat(result.getFirstName()).isEqualTo("Alexander");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getSpecialization()).isEqualTo("Fitness");

        verify(trainerRepository).save(argThat(trainer ->
                trainer.getId().equals(1L)
                        && trainer.getUser().getId().equals(10L)
                        && trainer.getUser().getFirstName().equals("Alexander")
                        && trainer.getUser().getLastName().equals("Brown")
                        && trainer.getUser().getIsActive().equals(false)
                        && trainer.getSpecialization().getName().equals("Fitness")
        ));
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerProfileDoesNotExist() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        String username = "Alex.Brown";

        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainer(username, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Trainer does not exist");

        verify(trainerRepository, never()).save(any());
        verifyNoInteractions(trainingTypeRepository);
    }

    @Test
    void updateTrainer_shouldThrowException_whenSpecializationDoesNotExist() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        String username = "Alex.Brown";

        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setIsActive(true);
        dto.setSpecialization("Missing");

        User user = user(10L, "Alex", "Brown", "Alex.Brown", "password12", true);
        Trainer trainer = trainer(1L, user, trainingType(5L, "Yoga"));
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainer(username, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Specialization not found");

        verify(trainerRepository, never()).save(any());
    }

    private static AuthenticationRequestDto auth(String username, String password) {
        AuthenticationRequestDto auth = new AuthenticationRequestDto();
        auth.setUsername(username);
        auth.setPassword(password);
        return auth;
    }


    @Test
    void getNotAssignedToTrainee_shouldReturnMappedTrainerDtos() {
        Trainer first = trainer(1L, user(10L, "Alex", "Brown", "Alex.Brown", "password12", true), trainingType(5L, "Yoga"));
        Trainer second = trainer(2L, user(20L, "Emma", "Wilson", "Emma.Wilson", "password12", true), trainingType(6L, "Fitness"));
        when(trainerRepository.getNotAssignedToTrainee("John.Smith")).thenReturn(List.of(first, second));

        List<TrainerDto> result = trainerService.getNotAssignedToTrainee("John.Smith");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TrainerDto::getUsername).containsExactly("Alex.Brown", "Emma.Wilson");
        assertThat(result).extracting(TrainerDto::getSpecialization).containsExactly("Yoga", "Fitness");
        verify(trainerRepository).getNotAssignedToTrainee("John.Smith");
    }

    @Test
    void searchTrainings_shouldDelegateCriteriaToRepository() {
        TrainerTrainingsSearchCriteria criteria = new TrainerTrainingsSearchCriteria();
        criteria.setFromDate(LocalDate.parse("2026-05-01"));
        criteria.setToDate(LocalDate.parse("2026-05-31"));
        criteria.setTraineeFirstName("John");
        criteria.setTraineeLastName("Smith");

        Training training = training(1L, "Morning Yoga", trainingType(5L, "Yoga"));
        when(trainerRepository.getTrainingsByCriteria(criteria, "Alex.Brown")).thenReturn(List.of(training));

        List<TrainingDto> result = trainerService.searchTrainings(criteria, "Alex.Brown");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Morning Yoga");
        assertThat(result.get(0).getType().getName()).isEqualTo("Yoga");
        verify(trainerRepository).getTrainingsByCriteria(criteria, "Alex.Brown");
    }

    @Test
    void changeIsActiveStatus_shouldReturnUpdatedTrainer() {
        User user = user(10L, "Alex", "Brown", "Alex.Brown", "password12", true);
        Trainer trainer = trainer(1L, user, trainingType(5L, "Yoga"));

        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDto result = trainerService.changeIsActiveStatus("Alex.Brown");

        assertThat(result.getUsername()).isEqualTo("Alex.Brown");
        assertThat(result.getIsActive()).isFalse();

        verify(trainerRepository).getByUsername("Alex.Brown");
        verify(trainerRepository).save(trainer);
    }

    private static User user(Long id, String firstName, String lastName, String username, String password, boolean active) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(active);
        return user;
    }


    private static Training training(Long id, String name, TrainingType type) {
        Training training = new Training();
        training.setId(id);
        training.setName(name);
        training.setType(type);
        training.setDate(LocalDate.parse("2026-05-20"));
        training.setDuration(60L);
        return training;
    }

    private static TrainingType trainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setName(name);
        return trainingType;
    }

    private static Trainer trainer(Long id, User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }
}
