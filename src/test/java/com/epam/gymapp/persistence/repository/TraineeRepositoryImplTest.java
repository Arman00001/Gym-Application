package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Trainee> traineeQuery;

    private TraineeRepositoryImpl traineeRepository;

    @BeforeEach
    void setUp() {
        traineeRepository = new TraineeRepositoryImpl();
        traineeRepository.setEntityManager(entityManager);
        lenient().when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void save_shouldPersistTraineeAndReturnSameEntity() {
        Trainee trainee = trainee(null, user(10L, "John.Smith"));
        doAnswer(invocation -> {
            trainee.setId(1L);
            return null;
        }).when(entityManager).persist(trainee);

        Trainee saved = traineeRepository.save(trainee);

        assertThat(saved).isSameAs(trainee);
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getUser().getId()).isEqualTo(10L);
        verify(transaction).begin();
        verify(entityManager).persist(trainee);
        verify(transaction).commit();
    }

    @Test
    void getByUserId_shouldReturnOptionalWithTrainee_whenExists() {
        Trainee trainee = trainee(1L, user(10L, "John.Smith"));
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("userId", 10L)).thenReturn(traineeQuery);
        when(traineeQuery.getSingleResultOrNull()).thenReturn(trainee);

        Optional<Trainee> result = traineeRepository.getByUserId(10L);

        assertThat(result).contains(trainee);
    }

    @Test
    void getByUserId_shouldReturnEmptyOptional_whenDoesNotExist() {
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("userId", 999L)).thenReturn(traineeQuery);
        when(traineeQuery.getSingleResultOrNull()).thenReturn(null);

        assertThat(traineeRepository.getByUserId(999L)).isEmpty();
    }

    @Test
    void getByUsername_shouldReturnOptionalWithTrainee_whenExists() {
        Trainee trainee = trainee(1L, user(10L, "John.Smith"));
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", "John.Smith")).thenReturn(traineeQuery);
        when(traineeQuery.getSingleResultOrNull()).thenReturn(trainee);

        assertThat(traineeRepository.getByUsername("John.Smith")).contains(trainee);
    }

    @Test
    void get_shouldReturnOptionalWithTrainee_whenExists() {
        Trainee trainee = trainee(1L, user(10L, "John.Smith"));
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);

        assertThat(traineeRepository.get(1L)).contains(trainee);
    }

    @Test
    void update_shouldMergeExistingTrainee() {
        Trainee trainee = trainee(1L, user(10L, "John.Smith"));
        trainee.setAddress("New address");
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        when(entityManager.merge(trainee)).thenReturn(trainee);

        Trainee updated = traineeRepository.update(trainee);

        assertThat(updated.getAddress()).isEqualTo("New address");
        verify(entityManager).merge(trainee);
        verify(transaction).commit();
    }

    @Test
    void update_shouldThrowException_whenTraineeDoesNotExist() {
        Trainee trainee = trainee(999L, user(10L, "John.Smith"));
        when(entityManager.find(Trainee.class, 999L)).thenReturn(null);
        when(transaction.isActive()).thenReturn(true);

        assertThatThrownBy(() -> traineeRepository.update(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee does not exist");
        verify(transaction).rollback();
    }

    @Test
    void delete_shouldRemoveTraineeByIdAndReturnRemovedEntity() {
        Trainee trainee = trainee(1L, user(10L, "John.Smith"));
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);

        Trainee deleted = traineeRepository.delete(1L);

        assertThat(deleted).isSameAs(trainee);
        verify(entityManager).remove(trainee);
        verify(transaction).commit();
    }

    @Test
    void deleteByUsername_shouldExecuteDeleteQuery() {
        when(entityManager.createQuery(anyString())).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", "John.Smith")).thenReturn(traineeQuery);
        when(traineeQuery.executeUpdate()).thenReturn(1);

        traineeRepository.deleteByUsername("John.Smith");

        verify(traineeQuery).executeUpdate();
        verify(transaction).commit();
    }

    @Test
    void getAll_shouldReturnAllTrainees() {
        List<Trainee> trainees = List.of(trainee(1L, user(10L, "John.Smith")), trainee(2L, user(20L, "Jane.Smith")));
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(trainees);

        assertThat(traineeRepository.getAll()).containsExactlyElementsOf(trainees);
    }

    private static Trainee trainee(Long id, User user) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.parse("2000-01-01"));
        trainee.setAddress("New York");
        return trainee;
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername(username);
        user.setPassword("password12");
        user.setIsActive(true);
        return user;
    }
}
