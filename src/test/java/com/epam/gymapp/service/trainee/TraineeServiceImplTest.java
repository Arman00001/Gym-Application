package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.util.PasswordGenerator;
import com.epam.gymapp.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createTrainee_shouldGenerateUsernameAndPasswordAndSaveTrainee() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setAddress("New York");
        dto.setDateOfBirth(OffsetDateTime.parse("2000-01-01T00:00:00Z"));

        when(usernameGenerator.generate("John", "Smith")).thenReturn("John.Smith");
        when(passwordGenerator.generate()).thenReturn("password12");

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            trainee.setId(1L);
            trainee.setUserId(1L);
            trainee.setIsActive(true);
            return trainee;
        });

        TraineeCreateResponse response = traineeService.createTrainee(dto);

        assertThat(response.getUsername()).isEqualTo("John.Smith");
        assertThat(response.getPassword()).isEqualTo("password12");

        verify(usernameGenerator).generate("John", "Smith");
        verify(passwordGenerator).generate();
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void getTrainee_shouldReturnDto_whenTraineeExists() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Smith");
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setIsActive(true);
        trainee.setAddress("New York");

        when(traineeRepository.get("John.Smith")).thenReturn(trainee);

        TraineeDto result = traineeService.getTrainee("John.Smith");

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAddress()).isEqualTo("New York");
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    void getTrainee_shouldThrowException_whenTraineeDoesNotExist() {
        when(traineeRepository.get("missing")).thenReturn(null);

        assertThatThrownBy(() -> traineeService.getTrainee("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
    }

    @Test
    void updateTrainee_shouldPreserveIdUserIdAndPassword() {
        Trainee existing = new Trainee();
        existing.setId(10L);
        existing.setUserId(20L);
        existing.setUsername("John.Smith");
        existing.setPassword("oldPassword");
        existing.setFirstName("John");
        existing.setLastName("Smith");
        existing.setIsActive(true);
        existing.setAddress("Old address");

        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setUsername("John.Smith");
        dto.setFirstName("Johnny");
        dto.setLastName("Smith");
        dto.setIsActive(false);
        dto.setAddress("New address");

        when(traineeRepository.get("John.Smith")).thenReturn(existing);
        when(traineeRepository.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TraineeDto result = traineeService.updateTrainee(dto);

        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getAddress()).isEqualTo("New address");

        verify(traineeRepository).update(argThat(trainee ->
                trainee.getId().equals(10L)
                        && trainee.getUserId().equals(20L)
                        && trainee.getPassword().equals("oldPassword")
        ));
    }

    @Test
    void deleteTrainee_shouldCallRepositoryDelete() {
        traineeService.deleteTrainee("John.Smith");

        verify(traineeRepository).delete("John.Smith");
    }
}