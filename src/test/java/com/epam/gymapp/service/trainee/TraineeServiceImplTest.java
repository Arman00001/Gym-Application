package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
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

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createTrainee_shouldCreateUserAndSaveTraineeProfile() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setAddress("New York");
        dto.setDateOfBirth(OffsetDateTime.parse("2000-01-01T00:00:00Z"));

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
        Trainee trainee = trainee(1L, user, "New York", "2000-01-01T00:00:00Z");
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.of(trainee));

        TraineeDto result = traineeService.getTraineeByUsername("John.Smith");

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAddress()).isEqualTo("New York");
        assertThat(result.getIsActive()).isTrue();
        verify(traineeRepository).getByUsername("John.Smith");
        verifyNoInteractions(userService);
    }

    @Test
    void getTraineeByUsername_shouldThrowException_whenTraineeProfileDoesNotExist() {
        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.getTraineeByUsername("John.Smith"))
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
        dto.setFirstName("Johnny");
        dto.setLastName("Smith");
        dto.setIsActive(false);
        dto.setAddress("New address");
        dto.setDateOfBirth(OffsetDateTime.parse("2001-01-01T00:00:00Z"));

        User user = user(10L, "John", "Smith", "John.Smith", "password12", true);
        Trainee existing = trainee(1L, user, "Old address", "2000-01-01T00:00:00Z");
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
                        && trainee.getDateOfBirth().equals(OffsetDateTime.parse("2001-01-01T00:00:00Z"))
        ));
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeProfileDoesNotExist() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setUsername("John.Smith");

        when(traineeRepository.getByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.updateTrainee(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");

        verify(traineeRepository, never()).update(any());
        verifyNoInteractions(userService);
    }

    @Test
    void deleteTraineeByUsername_shouldDelegateToRepository() {
        traineeService.deleteTraineeByUsername("John.Smith");

        verify(traineeRepository).deleteByUsername("John.Smith");
        verifyNoInteractions(userService);
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

    private static Trainee trainee(Long id, User user, String address, String dateOfBirth) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(user);
        trainee.setAddress(address);
        trainee.setDateOfBirth(OffsetDateTime.parse(dateOfBirth));
        return trainee;
    }
}
