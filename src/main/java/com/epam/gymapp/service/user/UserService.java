package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.user.CreatedUserResult;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.persistence.entity.Role;
import com.epam.gymapp.persistence.entity.User;

public interface UserService {
    CreatedUserResult createUser(UserCreateDto dto, Role role);
    User getByUsername(String username);
    User updateUser(UserUpdateDto dto);
    void deleteUser(Long id);
    void deleteUser(User user);
    User getById(Long id);
    void changePassword(String username, ChangePasswordRequestDto dto);
}
