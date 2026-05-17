package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.persistence.entity.User;

public interface UserService {
    User createUser(UserCreateDto dto);
    User getByUsername(String username);
    User updateUser(UserUpdateDto dto);
    void deleteUser(Long id);
    User getById(Long id);
}
