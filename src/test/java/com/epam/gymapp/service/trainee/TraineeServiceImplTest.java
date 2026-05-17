package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
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

        User user = new User();
        user.setId(10L);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("John.Smith");
        user.setPassword("password12");
        user.setIsActive(true);

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

        assertThat(savedTrainee.getUserId()).isEqualTo(10L);
        assertThat(savedTrainee.getDateOfBirth()).isEqualTo(dto.getDateOfBirth());
        assertThat(savedTrainee.getAddress()).isEqualTo("New York");

        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    void getTrainee_shouldReturnDto_whenUserAndTraineeExist() {
        User user = new User();
        user.setId(10L);
        user.setUsername("John.Smith");
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUserId(10L);
        trainee.setAddress("New York");
        trainee.setDateOfBirth(OffsetDateTime.parse("2000-01-01T00:00:00Z"));

        when(userService.getByUsername("John.Smith")).thenReturn(user);
        when(traineeRepository.getByUserId(10L)).thenReturn(trainee);

        TraineeDto result = traineeService.getTraineeByUsername("John.Smith");

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAddress()).isEqualTo("New York");
        assertThat(result.getIsActive()).isTrue();

        verify(userService).getByUsername("John.Smith");
        verify(traineeRepository).getByUserId(10L);
    }

    @Test
    void getTrainee_shouldThrowException_whenTraineeProfileDoesNotExist() {
        User user = new User();
        user.setId(10L);
        user.setUsername("John.Smith");

        when(userService.getByUsername("John.Smith")).thenReturn(user);
        when(traineeRepository.getByUserId(10L)).thenReturn(null);

        assertThatThrownBy(() -> traineeService.getTraineeByUsername("John.Smith"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
    }

    @Test
    void updateTrainee_shouldUpdateUserAndTraineeProfile() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setUsername("John.Smith");
        dto.setFirstName("Johnny");
        dto.setLastName("Smith");
        dto.setIsActive(false);
        dto.setAddress("New address");
        dto.setDateOfBirth(OffsetDateTime.parse("2001-01-01T00:00:00Z"));

        User existingUser = new User();
        existingUser.setId(10L);
        existingUser.setUsername("John.Smith");
        existingUser.setFirstName("John");
        existingUser.setLastName("Smith");
        existingUser.setIsActive(true);

        User updatedUser = new User();
        updatedUser.setId(10L);
        updatedUser.setUsername("John.Smith");
        updatedUser.setFirstName("Johnny");
        updatedUser.setLastName("Smith");
        updatedUser.setIsActive(false);

        Trainee existingTrainee = new Trainee();
        existingTrainee.setId(1L);
        existingTrainee.setUserId(10L);
        existingTrainee.setAddress("Old address");

        when(userService.getByUsername("John.Smith")).thenReturn(existingUser);
        when(userService.updateUser(any(UserUpdateDto.class))).thenReturn(updatedUser);
        when(traineeRepository.getByUserId(10L)).thenReturn(existingTrainee);
        when(traineeRepository.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TraineeDto result = traineeService.updateTrainee(dto);

        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getAddress()).isEqualTo("New address");

        verify(userService).getByUsername("John.Smith");
        verify(userService).updateUser(any(UserUpdateDto.class));

        verify(traineeRepository).update(argThat(trainee ->
                trainee.getId().equals(1L)
                        && trainee.getUserId().equals(10L)
                        && trainee.getAddress().equals("New address")
        ));
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeProfileDoesNotExist() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setUsername("John.Smith");
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setIsActive(true);
        dto.setAddress("New address");
        dto.setDateOfBirth(OffsetDateTime.parse("2001-01-01T00:00:00Z"));

        User existingUser = new User();
        existingUser.setId(10L);
        existingUser.setUsername("John.Smith");

        User updatedUser = new User();
        updatedUser.setId(10L);
        updatedUser.setUsername("John.Smith");
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Smith");
        updatedUser.setIsActive(true);

        when(userService.getByUsername("John.Smith")).thenReturn(existingUser);
        when(userService.updateUser(any(UserUpdateDto.class))).thenReturn(updatedUser);
        when(traineeRepository.getByUserId(10L)).thenReturn(null);

        assertThatThrownBy(() -> traineeService.updateTrainee(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");

        verify(userService).getByUsername("John.Smith");
        verify(userService).updateUser(any(UserUpdateDto.class));
        verify(traineeRepository).getByUserId(10L);
        verify(traineeRepository, never()).update(any());
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteTraineeProfileAndUser() {
        User user = new User();
        user.setId(10L);
        user.setUsername("John.Smith");

        when(userService.getByUsername("John.Smith")).thenReturn(user);

        traineeService.deleteTraineeByUsername("John.Smith");

        verify(userService).getByUsername("John.Smith");
        verify(traineeRepository).deleteByUserId(10L);
        verify(userService).deleteUser(10L);

        verify(traineeRepository, never()).delete(any());
        verify(traineeRepository, never()).getByUserId(any());
    }

    @Test
    void deleteTraineeByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userService.getByUsername("missing")).thenReturn(null);

        assertThatThrownBy(() -> traineeService.deleteTraineeByUsername("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");

        verify(userService).getByUsername("missing");
        verify(traineeRepository, never()).deleteByUserId(any());
        verify(userService, never()).deleteUser(any());
    }
}