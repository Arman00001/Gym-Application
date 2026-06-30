package com.epam.gymapp.service.authentication;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.user.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(AuthenticationRequestDto dto);
}
