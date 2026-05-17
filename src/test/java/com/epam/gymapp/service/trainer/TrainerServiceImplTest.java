package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createTrainer_shouldCreateUserAndSaveTrainerProfile() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setSpecialization("Yoga");

        User user = new User();
        user.setId(10L);
        user.setFirstName("Alex");
        user.setLastName("Brown");
        user.setUsername("Alex.Brown");
        user.setPassword("password12");
        user.setIsActive(true);

        when(userService.createUser(any(UserCreateDto.class))).thenReturn(user);

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

        assertThat(savedTrainer.getUserId()).isEqualTo(10L);
        assertThat(savedTrainer.getSpecialization()).isEqualTo("Yoga");

        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    void getTrainer_shouldReturnDto_whenUserAndTrainerExist() {
        User user = new User();
        user.setId(10L);
        user.setUsername("Alex.Brown");
        user.setFirstName("Alex");
        user.setLastName("Brown");
        user.setIsActive(true);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUserId(10L);
        trainer.setSpecialization("Yoga");

        when(userService.getByUsername("Alex.Brown")).thenReturn(user);
        when(trainerRepository.getByUserId(10L)).thenReturn(trainer);

        TrainerDto result = trainerService.getTrainerByUsername("Alex.Brown");

        assertThat(result.getUsername()).isEqualTo("Alex.Brown");
        assertThat(result.getFirstName()).isEqualTo("Alex");
        assertThat(result.getLastName()).isEqualTo("Brown");
        assertThat(result.getSpecialization()).isEqualTo("Yoga");
        assertThat(result.getIsActive()).isTrue();

        verify(userService).getByUsername("Alex.Brown");
        verify(trainerRepository).getByUserId(10L);
    }

    @Test
    void getTrainer_shouldThrowException_whenTrainerProfileDoesNotExist() {
        User user = new User();
        user.setId(10L);
        user.setUsername("Alex.Brown");

        when(userService.getByUsername("Alex.Brown")).thenReturn(user);
        when(trainerRepository.getByUserId(10L)).thenReturn(null);

        assertThatThrownBy(() -> trainerService.getTrainerByUsername("Alex.Brown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");
    }

    @Test
    void updateTrainer_shouldUpdateUserAndTrainerProfile() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setUsername("Alex.Brown");
        dto.setFirstName("Alexander");
        dto.setLastName("Brown");
        dto.setIsActive(false);
        dto.setSpecialization("Fitness");

        User existingUser = new User();
        existingUser.setId(10L);
        existingUser.setUsername("Alex.Brown");
        existingUser.setFirstName("Alex");
        existingUser.setLastName("Brown");
        existingUser.setIsActive(true);

        User updatedUser = new User();
        updatedUser.setId(10L);
        updatedUser.setUsername("Alex.Brown");
        updatedUser.setFirstName("Alexander");
        updatedUser.setLastName("Brown");
        updatedUser.setIsActive(false);

        Trainer existingTrainer = new Trainer();
        existingTrainer.setId(1L);
        existingTrainer.setUserId(10L);
        existingTrainer.setSpecialization("Yoga");

        when(userService.getByUsername("Alex.Brown")).thenReturn(existingUser);
        when(userService.updateUser(any(UserUpdateDto.class))).thenReturn(updatedUser);
        when(trainerRepository.getByUserId(10L)).thenReturn(existingTrainer);
        when(trainerRepository.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDto result = trainerService.updateTrainer(dto);

        assertThat(result.getFirstName()).isEqualTo("Alexander");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getSpecialization()).isEqualTo("Fitness");

        verify(userService).getByUsername("Alex.Brown");
        verify(userService).updateUser(any(UserUpdateDto.class));

        verify(trainerRepository).update(argThat(trainer ->
                trainer.getId().equals(1L)
                        && trainer.getUserId().equals(10L)
                        && trainer.getSpecialization().equals("Fitness")
        ));
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerProfileDoesNotExist() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setUsername("Alex.Brown");
        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setIsActive(true);
        dto.setSpecialization("Fitness");

        User existingUser = new User();
        existingUser.setId(10L);
        existingUser.setUsername("Alex.Brown");
        existingUser.setFirstName("Alex");
        existingUser.setLastName("Brown");
        existingUser.setIsActive(true);

        User updatedUser = new User();
        updatedUser.setId(10L);
        updatedUser.setUsername("Alex.Brown");
        updatedUser.setFirstName("Alex");
        updatedUser.setLastName("Brown");
        updatedUser.setIsActive(true);

        when(userService.getByUsername("Alex.Brown")).thenReturn(existingUser);
        when(userService.updateUser(any(UserUpdateDto.class))).thenReturn(updatedUser);
        when(trainerRepository.getByUserId(10L)).thenReturn(null);

        assertThatThrownBy(() -> trainerService.updateTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");

        verify(userService).getByUsername("Alex.Brown");
        verify(userService).updateUser(any(UserUpdateDto.class));
        verify(trainerRepository).getByUserId(10L);
        verify(trainerRepository, never()).update(any());
    }
}