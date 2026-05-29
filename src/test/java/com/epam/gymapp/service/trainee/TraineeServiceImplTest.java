package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.persistence.entity.*;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.service.user.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createTrainee_shouldCreateUserAndSaveTraineeProfile() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setAddress("New York");
        dto.setDateOfBirth(LocalDate.parse("2000-01-01"));

        User user = user(10L, "John", "Smith", "John.Smith", "password12", true);
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            trainee.setId(1L);
            return trainee;
        });

        TraineeCreateResponse response = traineeService.createTrainee(dto);

        assertThat(response.getUsername()).isEqualTo("John.Smith");
        assertThat(response.getPassword()).isEqualTo("password12");

        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).save(traineeCaptor.capture());
        Trainee savedTrainee = traineeCaptor.getValue();
        assertThat(savedTrainee.getUser()).isSameAs(user);
        assertThat(savedTrainee.getDateOfBirth()).isEqualTo(dto.getDateOfBirth());
        assertThat(savedTrainee.getAddress()).isEqualTo("New York");
        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    void getTraineeByUsername_shouldReturnDto_whenTraineeExists() {
        User user = user(10L, "John", "Smith", "John.Smith", "password12", true);
        Trainee trainee = trainee(1L, user, "New York", "2000-01-01");
        AuthenticationRequestDto auth = auth("John.Smith", "password12");
        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.of(trainee));

        TraineeDto result = traineeService.getTraineeByUsername(auth);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAddress()).isEqualTo("New York");
        assertThat(result.getIsActive()).isTrue();
        verify(traineeRepository).getByUsername("John.Smith");
        verify(userService).isAuthenticated("John.Smith", "password12");
    }

    @Test
    void getTraineeByUsername_shouldThrowException_whenTraineeProfileDoesNotExist() {
        AuthenticationRequestDto auth = auth("John.Smith", "password12");
        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getTraineeByUsername(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
    }

    @Test
    void getTraineeById_shouldReturnDto_whenTraineeExists() {
        User user = user(10L, "John", "Smith", "John.Smith", "password12", true);
        Trainee trainee = trainee(1L, user, "New York", "2000-01-01T00:00:00Z");
        when(traineeRepository.get(1L)).thenReturn(Optional.of(trainee));

        TraineeDto result = traineeService.getTraineeById(1L);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getAddress()).isEqualTo("New York");
        verify(traineeRepository).get(1L);
    }

    @Test
    void updateTrainee_shouldUpdateUserFieldsAndTraineeProfile() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setUsername("John.Smith");
        dto.setPassword("password12");
        dto.setFirstName("Johnny");
        dto.setLastName("Smith");
        dto.setIsActive(false);
        dto.setAddress("New address");
        dto.setDateOfBirth(LocalDate.parse("2001-01-01"));

        User user = user(10L, "John", "Smith", "John.Smith", "password12", true);
        Trainee existing = trainee(1L, user, "Old address", "2000-01-01");
        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.of(existing));
        when(traineeRepository.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TraineeDto result = traineeService.updateTrainee(dto);

        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getAddress()).isEqualTo("New address");
        verify(traineeRepository).update(argThat(trainee ->
                trainee.getId().equals(1L)
                        && trainee.getUser().getId().equals(10L)
                        && trainee.getUser().getFirstName().equals("Johnny")
                        && trainee.getAddress().equals("New address")
                        && trainee.getDateOfBirth().equals(LocalDate.parse("2001-01-01"))
        ));
        verify(userService).isAuthenticated("John.Smith", "password12");
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeProfileDoesNotExist() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setUsername("John.Smith");
        dto.setPassword("password12");

        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.updateTrainee(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");

        verify(traineeRepository, never()).update(any());
        verify(userService).isAuthenticated("John.Smith", "password12");
    }

    @Test
    void deleteTraineeByUsername_shouldDelegateToRepository() {
        DeleteRequestDto dto = new DeleteRequestDto();
        dto.setUsername("John.Smith");
        dto.setPassword("password12");
        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);

        traineeService.deleteTraineeByUsername(dto);

        verify(traineeRepository).deleteByUsername("John.Smith");
        verify(userService).isAuthenticated("John.Smith", "password12");
    }



    @Test
    void changeIsActiveStatus_shouldAuthenticateAndReturnUpdatedTrainee() {
        AuthenticationRequestDto auth = auth("John.Smith", "password12");
        User user = user(10L, "John", "Smith", "John.Smith", "password12", false);
        Trainee updated = trainee(1L, user, "New York", "2000-01-01");
        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);
        when(traineeRepository.changeIsActiveStatus("John.Smith")).thenReturn(updated);

        TraineeDto result = traineeService.changeIsActiveStatus(auth);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getIsActive()).isFalse();
        verify(traineeRepository).changeIsActiveStatus("John.Smith");
    }

    @Test
    void changeIsActiveStatus_shouldThrowException_whenCredentialsAreInvalid() {
        AuthenticationRequestDto auth = auth("John.Smith", "bad");
        when(userService.isAuthenticated("John.Smith", "bad")).thenReturn(false);

        assertThatThrownBy(() -> traineeService.changeIsActiveStatus(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incorrect Credentials");

        verify(traineeRepository, never()).changeIsActiveStatus(any());
    }

    @Test
    void searchTrainings_shouldAuthenticateAndDelegateCriteriaToRepository() {
        TraineeTrainingsSearchCriteria criteria = new TraineeTrainingsSearchCriteria();
        criteria.setUsername("John.Smith");
        criteria.setPassword("password12");
        criteria.setFromDate(LocalDate.parse("2026-05-01"));
        criteria.setToDate(LocalDate.parse("2026-05-31"));
        criteria.setTrainerFirstName("Alex");
        criteria.setTrainerLastName("Brown");
        criteria.setTrainingType("Yoga");

        Training training = training(1L, "Morning Yoga", trainingType(5L, "Yoga"));
        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);
        when(traineeRepository.getTrainingsByCriteria(criteria)).thenReturn(List.of(training));

        List<TrainingDto> result = traineeService.searchTrainings(criteria);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Morning Yoga");
        assertThat(result.get(0).getType().getName()).isEqualTo("Yoga");
        verify(traineeRepository).getTrainingsByCriteria(criteria);
    }

    @Test
    void searchTrainings_shouldThrowException_whenCredentialsAreInvalid() {
        TraineeTrainingsSearchCriteria criteria = new TraineeTrainingsSearchCriteria();
        criteria.setUsername("John.Smith");
        criteria.setPassword("bad");
        when(userService.isAuthenticated("John.Smith", "bad")).thenReturn(false);

        assertThatThrownBy(() -> traineeService.searchTrainings(criteria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incorrect Credentials");

        verify(traineeRepository, never()).getTrainingsByCriteria(any());
    }

    @Test
    void updateTrainerList_shouldAuthenticateReplaceTrainerListAndCommitTransaction() {
        TraineeTrainerListUpdateDto dto = new TraineeTrainerListUpdateDto();
        dto.setUsername("John.Smith");
        dto.setPassword("password12");
        dto.setTrainerUsernames(List.of("Alex.Brown", "Emma.Wilson"));

        Trainee trainee = trainee(1L, user(10L, "John", "Smith", "John.Smith", "password12", true), "New York", "2000-01-01");
        Trainer trainerOne = trainer(2L, user(20L, "Alex", "Brown", "Alex.Brown", "password12", true), trainingType(5L, "Yoga"));
        Trainer trainerTwo = trainer(3L, user(30L, "Emma", "Wilson", "Emma.Wilson", "password12", true), trainingType(6L, "Fitness"));

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(userService.isAuthenticated("John.Smith", "password12")).thenReturn(true);
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.getByUsernames(List.of("Alex.Brown", "Emma.Wilson"))).thenReturn(List.of(trainerOne, trainerTwo));

        TraineeDto result = traineeService.updateTrainerList(dto);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(trainee.getTrainers()).containsExactly(trainerOne, trainerTwo);
        verify(transaction).begin();
        verify(transaction).commit();
        verify(transaction, never()).rollback();
    }

    @Test
    void updateTrainerList_shouldRollbackAndThrowException_whenCredentialsAreInvalid() {
        TraineeTrainerListUpdateDto dto = new TraineeTrainerListUpdateDto();
        dto.setUsername("John.Smith");
        dto.setPassword("bad");
        dto.setTrainerUsernames(List.of("Alex.Brown"));

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(userService.isAuthenticated("John.Smith", "bad")).thenReturn(false);
        when(transaction.isActive()).thenReturn(true);

        assertThatThrownBy(() -> traineeService.updateTrainerList(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incorrect Credentials");

        verify(transaction).begin();
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verifyNoInteractions(trainerRepository);
    }

    private static AuthenticationRequestDto auth(String username, String password) {
        AuthenticationRequestDto auth = new AuthenticationRequestDto();
        auth.setUsername(username);
        auth.setPassword(password);
        return auth;
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

    private static Training training(Long id, String name, TrainingType type) {
        Training training = new Training();
        training.setId(id);
        training.setName(name);
        training.setType(type);
        training.setDate(LocalDate.parse("2026-05-20"));
        training.setDuration(60L);
        return training;
    }

    private static Trainee trainee(Long id, User user, String address, String dateOfBirth) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(user);
        trainee.setAddress(address);
        trainee.setDateOfBirth(LocalDate.parse(dateOfBirth.contains("T") ? dateOfBirth.substring(0, dateOfBirth.indexOf("T")) : dateOfBirth));
        return trainee;
    }
}
