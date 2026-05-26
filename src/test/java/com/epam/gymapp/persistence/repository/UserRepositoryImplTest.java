package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<User> userQuery;

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
        userRepository.setEntityManager(entityManager);
        lenient().when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void save_shouldPersistUserAndReturnSameEntity() {
        User user = user(null, "John", "Smith", "John.Smith");
        doAnswer(invocation -> {
            user.setId(1L);
            return null;
        }).when(entityManager).persist(user);

        User saved = userRepository.save(user);

        assertThat(saved).isSameAs(user);
        assertThat(saved.getId()).isEqualTo(1L);
        verify(transaction).begin();
        verify(entityManager).persist(user);
        verify(transaction).commit();
    }

    @Test
    void getById_shouldReturnOptionalWithUser_whenExists() {
        User user = user(1L, "John", "Smith", "John.Smith");
        when(entityManager.find(User.class, 1L)).thenReturn(user);

        Optional<User> result = userRepository.getById(1L);

        assertThat(result).contains(user);
    }

    @Test
    void getByUsername_shouldReturnOptionalWithUser_whenExists() {
        User user = user(1L, "John", "Smith", "John.Smith");
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter("username", "John.Smith")).thenReturn(userQuery);
        when(userQuery.getSingleResultOrNull()).thenReturn(user);

        Optional<User> result = userRepository.getByUsername("John.Smith");

        assertThat(result).contains(user);
        verify(userQuery).setParameter("username", "John.Smith");
    }

    @Test
    void getByUsername_shouldReturnEmptyOptional_whenDoesNotExist() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter("username", "missing")).thenReturn(userQuery);
        when(userQuery.getSingleResultOrNull()).thenReturn(null);

        Optional<User> result = userRepository.getByUsername("missing");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter("username", "John.Smith")).thenReturn(userQuery);
        when(userQuery.getSingleResultOrNull()).thenReturn(user(1L, "John", "Smith", "John.Smith"));

        assertThat(userRepository.existsByUsername("John.Smith")).isTrue();
    }

    @Test
    void update_shouldMergeExistingUser() {
        User user = user(1L, "Johnny", "Smith", "John.Smith");
        when(entityManager.find(User.class, 1L)).thenReturn(user);
        when(entityManager.merge(user)).thenReturn(user);

        User updated = userRepository.update(user);

        assertThat(updated).isSameAs(user);
        verify(transaction).begin();
        verify(entityManager).merge(user);
        verify(transaction).commit();
    }

    @Test
    void update_shouldThrowException_whenUserDoesNotExist() {
        User user = user(999L, "John", "Smith", "John.Smith");
        when(entityManager.find(User.class, 999L)).thenReturn(null);
        when(transaction.isActive()).thenReturn(true);

        assertThatThrownBy(() -> userRepository.update(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User does not exist");
        verify(transaction).rollback();
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = user(1L, "John", "Smith", "John.Smith");
        when(entityManager.find(User.class, 1L)).thenReturn(user);

        userRepository.delete(1L);

        verify(entityManager).remove(user);
        verify(transaction).commit();
    }

    private static User user(Long id, String firstName, String lastName, String username) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("password12");
        user.setIsActive(true);
        return user;
    }
}
