package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.TraineeTrainer;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee_trainer.TraineeTrainerRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeTrainerRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query deleteQuery;

    private TraineeTrainerRepositoryImpl traineeTrainerRepository;

    @BeforeEach
    void setUp() {
        traineeTrainerRepository = new TraineeTrainerRepositoryImpl();
        traineeTrainerRepository.setEntityManager(entityManager);
    }

    @Test
    void updateTrainerList_shouldDeleteExistingAssignmentsAndPersistNewOnes() {
        Trainee trainee = trainee(1L, user(10L, "John.Smith"));
        Trainer trainerOne = trainer(2L, user(20L, "Alex.Brown"), trainingType(5L, "Yoga"));
        Trainer trainerTwo = trainer(3L, user(30L, "Emma.Wilson"), trainingType(6L, "Fitness"));

        when(entityManager.createQuery("DELETE FROM TraineeTrainer tt WHERE tt.trainee.id = :traineeId"))
                .thenReturn(deleteQuery);
        when(deleteQuery.setParameter("traineeId", 1L)).thenReturn(deleteQuery);
        when(deleteQuery.executeUpdate()).thenReturn(2);

        traineeTrainerRepository.updateTrainerList(trainee, List.of(trainerOne, trainerTwo));

        verify(deleteQuery).executeUpdate();

        ArgumentCaptor<TraineeTrainer> captor = ArgumentCaptor.forClass(TraineeTrainer.class);

        verify(entityManager, times(2)).persist(captor.capture());

        List<TraineeTrainer> savedAssignments = captor.getAllValues();

        assertThat(savedAssignments)
                .extracting(TraineeTrainer::getTrainee)
                .containsExactly(trainee, trainee);

        assertThat(savedAssignments)
                .extracting(TraineeTrainer::getTrainer)
                .containsExactly(trainerOne, trainerTwo);

        List<TraineeTrainer> assignments = captor.getAllValues();
        assertThat(assignments).hasSize(2);
        assertThat(assignments).extracting(TraineeTrainer::getTrainee)
                .containsExactly(trainee, trainee);
        assertThat(assignments).extracting(TraineeTrainer::getTrainer)
                .containsExactly(trainerOne, trainerTwo);
    }

    @Test
    void updateTrainerList_shouldOnlyDeleteExistingAssignments_whenNewTrainerListIsEmpty() {
        Trainee trainee = trainee(1L, user(10L, "John.Smith"));

        when(entityManager.createQuery("DELETE FROM TraineeTrainer tt WHERE tt.trainee.id = :traineeId"))
                .thenReturn(deleteQuery);
        when(deleteQuery.setParameter("traineeId", 1L)).thenReturn(deleteQuery);
        when(deleteQuery.executeUpdate()).thenReturn(2);

        traineeTrainerRepository.updateTrainerList(trainee, List.of());

        verify(deleteQuery).executeUpdate();
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
        TrainingType type = new TrainingType();
        type.setId(id);
        type.setName(name);
        return type;
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setFirstName(username.substring(0, username.indexOf('.')));
        user.setLastName(username.substring(username.indexOf('.') + 1));
        user.setUsername(username);
        user.setPassword("password12");
        user.setIsActive(true);
        return user;
    }
}
