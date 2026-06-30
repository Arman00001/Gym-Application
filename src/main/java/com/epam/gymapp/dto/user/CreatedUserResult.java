package com.epam.gymapp.dto.user;

import com.epam.gymapp.persistence.entity.User;

public record CreatedUserResult(User user, String rawPassword){}