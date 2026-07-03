package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.dto.user.CreatedUserResult;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.persistence.entity.Role;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import com.epam.gymapp.util.PasswordGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldGenerateUsernamePasswordAndSetActive() {
        UserCreateDto dto = new UserCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        when(userRepository.existsByUsername("John.Smith")).thenReturn(false);
        when(passwordGenerator.generate()).thenReturn("password12");
        when(passwordEncoder.encode("password12")).thenReturn("hashedPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        CreatedUserResult createdUserResult = userService.createUser(dto, null);
        User result = createdUserResult.user();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(createdUserResult.rawPassword()).isEqualTo("password12");
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getPassword()).isEqualTo("hashedPassword");
        assertThat(result.getPassword()).isNotEqualTo("password12");


        verify(userRepository).existsByUsername("John.Smith");
        verify(passwordGenerator).generate();
        verify(passwordEncoder).encode("password12");
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

        CreatedUserResult createdUserResult = userService.createUser(dto, null);
        User result = createdUserResult.user();

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

        CreatedUserResult createdUserResult = userService.createUser(dto, null);
        User result = createdUserResult.user();

        assertThat(result.getUsername()).isEqualTo("John.Smith2");
    }

    @Test
    void createUser_shouldAssignRole_whenRoleProvided() {
        UserCreateDto dto = new UserCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        Role role = Role.ADMIN;

        when(userRepository.existsByUsername("John.Smith")).thenReturn(false);
        when(passwordGenerator.generate()).thenReturn("password12");
        when(passwordEncoder.encode("password12")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreatedUserResult createdUserResult = userService.createUser(dto, role);

        assertThat(createdUserResult.user().getRole()).isSameAs(role);
    }

    @Test
    void getByUsername_shouldReturnUser_whenExists() {
        User user = new User();
        user.setUsername("John.Smith");

        when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("John.Smith");

        assertThat(result).isSameAs(user);
        verify(userRepository).findByUsername("John.Smith");
    }

    @Test
    void getByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByUsername("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User does not exist");

        verify(userRepository).findByUsername("missing");
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
        existing.setIsActive(true);

        when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getFirstName()).isEqualTo("Johnny");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getIsActive()).isFalse();

        verify(userRepository).findByUsername("John.Smith");
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1L);
        dto.setUsername("John.Smith");

        when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User does not exist");

        verify(userRepository).findByUsername("John.Smith");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldEncodeNewPasswordAndSaveUser_whenOldPasswordMatches(){
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto();
        dto.setOldPassword("oldPassword");
        dto.setNewPassword("newPassword");

        User user = new User();
        user.setUsername("John.Smith");
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.changePassword("John.Smith", dto);

        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");

        verify(userRepository).findByUsername("John.Smith");
        verify(passwordEncoder).matches("oldPassword", "encodedOldPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrowBadCredentialsException_whenUserDoesNotExist() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto();
        dto.setOldPassword("oldPassword");
        dto.setNewPassword("newPassword");

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword("missing", dto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Incorrect Credentials");

        verify(userRepository).findByUsername("missing");
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowBadCredentialsException_whenOldPasswordDoesNotMatch() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto();
        dto.setOldPassword("wrongPassword");
        dto.setNewPassword("newPassword");

        User user = new User();
        user.setUsername("John.Smith");
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername("John.Smith")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("John.Smith", dto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Incorrect Credentials");

        verify(userRepository).findByUsername("John.Smith");
        verify(passwordEncoder).matches("wrongPassword", "encodedOldPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}