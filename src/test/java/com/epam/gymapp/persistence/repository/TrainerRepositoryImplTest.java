package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Trainer> trainerQuery;

    private TrainerRepositoryImpl trainerRepository;

    @BeforeEach
    void setUp() {
        trainerRepository = new TrainerRepositoryImpl();
        trainerRepository.setEntityManager(entityManager);
        lenient().when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void save_shouldPersistTrainerAndReturnSameEntity() {
        Trainer trainer = trainer(null, user(10L, "Alex.Brown"), trainingType(5L, "Yoga"));
        doAnswer(invocation -> {
            trainer.setId(1L);
            return null;
        }).when(entityManager).persist(trainer);

        Trainer saved = trainerRepository.save(trainer);

        assertThat(saved).isSameAs(trainer);
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUser().getId()).isEqualTo(10L);
        assertThat(saved.getSpecialization().getName()).isEqualTo("Yoga");
        verify(entityManager).persist(trainer);
        verify(transaction).commit();
    }

    @Test
    void getByUserId_shouldReturnOptionalWithTrainer_whenExists() {
        Trainer trainer = trainer(1L, user(10L, "Alex.Brown"), trainingType(5L, "Yoga"));
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("userId", 10L)).thenReturn(trainerQuery);
        when(trainerQuery.getSingleResultOrNull()).thenReturn(trainer);

        assertThat(trainerRepository.getByUserId(10L)).contains(trainer);
    }

    @Test
    void getByUserId_shouldReturnEmptyOptional_whenDoesNotExist() {
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("userId", 999L)).thenReturn(trainerQuery);
        when(trainerQuery.getSingleResultOrNull()).thenReturn(null);

        assertThat(trainerRepository.getByUserId(999L)).isEmpty();
    }

    @Test
    void getByUsername_shouldReturnOptionalWithTrainer_whenExists() {
        Trainer trainer = trainer(1L, user(10L, "Alex.Brown"), trainingType(5L, "Yoga"));
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", "Alex.Brown")).thenReturn(trainerQuery);
        when(trainerQuery.getSingleResultOrNull()).thenReturn(trainer);

        assertThat(trainerRepository.getByUsername("Alex.Brown")).contains(trainer);
    }

    @Test
    void get_shouldReturnOptionalWithTrainer_whenExists() {
        Trainer trainer = trainer(1L, user(10L, "Alex.Brown"), trainingType(5L, "Yoga"));
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        assertThat(trainerRepository.get(1L)).contains(trainer);
    }

    @Test
    void update_shouldMergeExistingTrainer() {
        Trainer trainer = trainer(1L, user(10L, "Alex.Brown"), trainingType(6L, "Fitness"));
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);
        when(entityManager.merge(trainer)).thenReturn(trainer);

        Trainer updated = trainerRepository.update(trainer);

        assertThat(updated.getSpecialization().getName()).isEqualTo("Fitness");
        verify(entityManager).merge(trainer);
        verify(transaction).commit();
    }

    @Test
    void update_shouldThrowException_whenTrainerDoesNotExist() {
        Trainer trainer = trainer(999L, user(10L, "Alex.Brown"), trainingType(5L, "Yoga"));
        when(entityManager.find(Trainer.class, 999L)).thenReturn(null);
        when(transaction.isActive()).thenReturn(true);

        assertThatThrownBy(() -> trainerRepository.update(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer does not exist");
        verify(transaction).rollback();
    }

    @Test
    void getAll_shouldReturnAllTrainers() {
        List<Trainer> trainers = List.of(
                trainer(1L, user(10L, "Alex.Brown"), trainingType(5L, "Yoga")),
                trainer(2L, user(20L, "Bob.Green"), trainingType(6L, "Fitness"))
        );
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(trainers);

        assertThat(trainerRepository.getAll()).containsExactlyElementsOf(trainers);
    }

    @Test
    void getNotAssignedToTrainee_shouldUseTraineeTrainerAssignments() {
        List<Trainer> trainers = List.of(
                trainer(1L, user(10L, "Alex.Brown"), trainingType(5L, "Yoga")),
                trainer(2L, user(20L, "Bob.Green"), trainingType(6L, "Fitness"))
        );
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", "John.Smith")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(trainers);

        assertThat(trainerRepository.getNotAssignedToTrainee("John.Smith")).containsExactlyElementsOf(trainers);

        verify(entityManager).createQuery(contains("TraineeTrainer"), eq(Trainer.class));
        verify(trainerQuery).setParameter("username", "John.Smith");
    }

    @Test
    void getAllByTraineeUsername_shouldReturnAssignedTrainersFromTraineeTrainer() {
        List<Trainer> trainers = List.of(
                trainer(1L, user(10L, "Alex.Brown"), trainingType(5L, "Yoga")),
                trainer(2L, user(20L, "Bob.Green"), trainingType(6L, "Fitness"))
        );
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("traineeUsername", "John.Smith")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(trainers);

        assertThat(trainerRepository.getAllByTraineeUsername("John.Smith")).containsExactlyElementsOf(trainers);

        verify(entityManager).createQuery(contains("FROM TraineeTrainer tt"), eq(Trainer.class));
        verify(trainerQuery).setParameter("traineeUsername", "John.Smith");
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
        user.setFirstName("Alex");
        user.setLastName("Brown");
        user.setUsername(username);
        user.setPassword("password12");
        user.setIsActive(true);
        return user;
    }
}
