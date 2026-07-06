package com.epam.gymapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
//    private Long id;
}
