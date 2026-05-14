package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.util.PasswordGenerator;
import com.epam.gymapp.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createTrainer_shouldGenerateUsernameAndPasswordAndSaveTrainer() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("Alex");
        dto.setLastName("Brown");
        dto.setSpecialization("Yoga");

        when(usernameGenerator.generate("Alex", "Brown")).thenReturn("Alex.Brown");
        when(passwordGenerator.generate()).thenReturn("password12");

        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            trainer.setId(1L);
            trainer.setUserId(1L);
            trainer.setIsActive(true);
            return trainer;
        });

        TrainerCreateResponse response = trainerService.createTrainer(dto);

        assertThat(response.getUsername()).isEqualTo("Alex.Brown");
        assertThat(response.getPassword()).isEqualTo("password12");

        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void getTrainer_shouldReturnDto_whenTrainerExists() {
        Trainer trainer = new Trainer();
        trainer.setUsername("Alex.Brown");
        trainer.setFirstName("Alex");
        trainer.setLastName("Brown");
        trainer.setIsActive(true);
        trainer.setSpecialization("Yoga");

        when(trainerRepository.get("Alex.Brown")).thenReturn(trainer);

        TrainerDto result = trainerService.getTrainer("Alex.Brown");

        assertThat(result.getUsername()).isEqualTo("Alex.Brown");
        assertThat(result.getFirstName()).isEqualTo("Alex");
        assertThat(result.getLastName()).isEqualTo("Brown");
        assertThat(result.getSpecialization()).isEqualTo("Yoga");
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    void getTrainer_shouldThrowException_whenTrainerDoesNotExist() {
        when(trainerRepository.get("missing")).thenReturn(null);

        assertThatThrownBy(() -> trainerService.getTrainer("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");
    }

    @Test
    void updateTrainer_shouldUpdateExistingTrainer() {
        Trainer existing = new Trainer();
        existing.setUsername("Alex.Brown");
        existing.setFirstName("Alex");
        existing.setLastName("Brown");
        existing.setIsActive(true);
        existing.setSpecialization("Yoga");

        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setUsername("Alex.Brown");
        dto.setFirstName("Alexander");
        dto.setLastName("Brown");
        dto.setIsActive(false);
        dto.setSpecialization("Fitness");

        when(trainerRepository.get("Alex.Brown")).thenReturn(existing);
        when(trainerRepository.update(existing)).thenReturn(existing);

        TrainerDto result = trainerService.updateTrainer(dto);

        assertThat(result.getFirstName()).isEqualTo("Alexander");
        assertThat(result.getIsActive()).isFalse();
        assertThat(result.getSpecialization()).isEqualTo("Fitness");

        verify(trainerRepository).update(existing);
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerDoesNotExist() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setUsername("missing");

        when(trainerRepository.get("missing")).thenReturn(null);

        assertThatThrownBy(() -> trainerService.updateTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");

        verify(trainerRepository, never()).update(any());
    }
}