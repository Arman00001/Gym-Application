package com.epam.gymapp.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}
