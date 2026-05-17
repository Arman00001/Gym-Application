package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import com.epam.gymapp.util.PasswordGenerator;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldGenerateUsernamePasswordAndSetActive() {
        UserCreateDto dto = new UserCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        when(userRepository.existsByUsername("John.Smith")).thenReturn(false);
        when(passwordGenerator.generate()).thenReturn("password12");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User result = userService.createUser(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getPassword()).isEqualTo("password12");
        assertThat(result.getIsActive()).isTrue();

        verify(userRepository).existsByUsername("John.Smith");
        verify(passwordGenerator).generate();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldAddSuffix_whenUsernameAlreadyExists() {
        UserCreateDto dto = new UserCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        when(userRepository.existsByUsername("John.Smith")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith1")).thenReturn(false);
        when(passwordGenerator.generate()).thenReturn("password12");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(dto);

        assertThat(result.getUsername()).isEqualTo("John.Smith1");

        verify(userRepository).existsByUsername("John.Smith");
        verify(userRepository).existsByUsername("John.Smith1");
    }

    @Test
    void createUser_shouldAddNextSuffix_whenSeveralUsernamesAlreadyExist() {
        UserCreateDto dto = new UserCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        when(userRepository.existsByUsername("John.Smith")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith1")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith2")).thenReturn(false);
        when(passwordGenerator.generate()).thenReturn("password12");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(dto);

        assertThat(result.getUsername()).isEqualTo("John.Smith2");
    }

    @Test
    void getByUsername_shouldReturnUser_whenExists() {
        User user = new User();
        user.setUsername("John.Smith");

        when(userRepository.getByUsername("John.Smith")).thenReturn(user);

        User result = userService.getByUsername("John.Smith");

        assertThat(result).isSameAs(user);
    }

    @Test
    void getByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.getByUsername("missing")).thenReturn(null);

        assertThatThrownBy(() -> userService.getByUsername("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User does not exist");
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1L);
        dto.setUsername("John.Smith");
        dto.setFirstName("Johnny");
        dto.setLastName("Smith");
        dto.setIsActive(false);

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("John.Smith");
        existing.setFirstName("John");
        existing.setLastName("Smith");
        existing.setPassword("oldPassword");
        existing.setIsActive(true);

        when(userRepository.getById(1L)).thenReturn(existing);
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getPassword()).isEqualTo("oldPassword");
        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getIsActive()).isFalse();

        verify(userRepository).getById(1L);
        verify(userRepository).update(existing);
        verify(userRepository, never()).getByUsername(any());
    }
}