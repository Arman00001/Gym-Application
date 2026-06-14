package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.persistence.entity.User;

public interface UserService {
    User createUser(UserCreateDto dto);
    User getByUsername(String username);
    User updateUser(UserUpdateDto dto);
    void deleteUser(Long id);
    void deleteUser(User user);
    User getById(Long id);
    boolean isAuthenticated(String username, String password);
    void changePassword(ChangePasswordRequestDto dto);
    boolean login(String username, String password);
}
