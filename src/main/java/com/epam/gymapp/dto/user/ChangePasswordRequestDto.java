package com.epam.gymapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDto {
    @NotBlank(message = "old password should not be blank")
    private String oldPassword;
    @NotBlank(message = "new password should not be blank")
    private String newPassword;
}
