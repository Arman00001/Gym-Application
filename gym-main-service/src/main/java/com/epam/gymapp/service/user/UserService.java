package com.epam.gymapp.service.user;

import com.epam.gymapp.dto.user.CreatedUserResult;
import com.epam.gymapp.dto.user.UserCreateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.persistence.entity.Role;
import com.epam.gymapp.persistence.entity.User;

/**
 * Service interface for managing users
 *
 * <p>
 * Defines operations for creating users, retrieving users by username,
 * updating user profile information, and changing user passwords.
 * </p>
 */
public interface UserService {
    /**
     * Creates a new user with the provided role.
     *
     * <p>
     * The created user result includes the saved user and the generated raw password.
     * </p>
     *
     * @param dto  the user creation data
     * @param role the role assigned to the new user
     * @return the created user result containing the saved user and generated raw password
     */
    CreatedUserResult createUser(UserCreateDto dto, Role role);

    /**
     * Retrieves a user by username.
     *
     * @param username the username of the user to find
     * @return the user with the given username
     * @throws ResourceNotFoundException if no user exists with the given username
     */
    User getByUsername(String username);

    /**
     * Updates an existing user's profile information.
     *
     * @param dto the user update data
     * @return the updated user
     * @throws ResourceNotFoundException if no user exists with the given username
     */
    User updateUser(UserUpdateDto dto);

    /**
     * Change user's password.
     *
     * @param username the username of the user whose password should be changed
     * @param dto      the password change request containing the old and new passwords
     */
    void changePassword(String username, ChangePasswordRequestDto dto);
}
