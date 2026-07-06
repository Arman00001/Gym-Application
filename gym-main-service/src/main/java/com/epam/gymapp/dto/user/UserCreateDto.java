package com.epam.gymapp.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    private String firstName;
    private String lastName;
    private String role;
}
