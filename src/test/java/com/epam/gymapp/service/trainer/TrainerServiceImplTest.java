package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import com.epam.gymapp.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        when(trainingTypeRepository.getByName("Yoga")).thenReturn(Optional.of(yoga));
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
        assertThat(savedTrainer.getUser()).isSameAs(user);
        assertThat(savedTrainer.getSpecialization()).isSameAs(yoga);
        verify(trainingTypeRepository).getByName("Yoga");
        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    void createTrainer_shouldThrowException_whenSpecializationDoesNotExist() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setSpecialization("Missing");
        when(trainingTypeRepository.getByName("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.createTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
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
        verifyNoInteractions(userService, trainingTypeRepository);
    }

    @Test
    void getTrainerByUsername_shouldThrowException_whenTrainerProfileDoesNotExist() {
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerByUsername("Alex.Brown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");
    }

    @Test
    void updateTrainer_shouldUpdateUserFieldsAndSpecialization() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setUsername("Alex.Brown");
        dto.setFirstName("Alexander");
        dto.setLastName("Brown");
        dto.setIsActive(false);
        dto.setSpecialization("Fitness");

        User user = user(10L, "Alex", "Brown", "Alex.Brown", "password12", true);
        TrainingType oldType = trainingType(5L, "Yoga");
        TrainingType newType = trainingType(6L, "Fitness");
        Trainer existingTrainer = trainer(1L, user, oldType);
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.of(existingTrainer));
        when(trainingTypeRepository.getByName("Fitness")).thenReturn(Optional.of(newType));
        when(trainerRepository.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDto result = trainerService.updateTrainer(dto);

        assertThat(result.getFirstName()).isEqualTo("Alexander");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getSpecialization()).isEqualTo("Fitness");
        verify(trainerRepository).update(argThat(trainer ->
                trainer.getId().equals(1L)
                        && trainer.getUser().getId().equals(10L)
                        && trainer.getUser().getFirstName().equals("Alexander")
                        && trainer.getSpecialization().getName().equals("Fitness")
        ));
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerProfileDoesNotExist() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setUsername("Alex.Brown");
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");

        verify(trainerRepository, never()).update(any());
        verifyNoInteractions(userService, trainingTypeRepository);
    }

    @Test
    void updateTrainer_shouldThrowException_whenSpecializationDoesNotExist() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setUsername("Alex.Brown");
        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setIsActive(true);
        dto.setSpecialization("Missing");

        User user = user(10L, "Alex", "Brown", "Alex.Brown", "password12", true);
        Trainer trainer = trainer(1L, user, trainingType(5L, "Yoga"));
        when(trainerRepository.getByUsername("Alex.Brown")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.getByName("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specialization not found");

        verify(trainerRepository, never()).update(any());
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
}
