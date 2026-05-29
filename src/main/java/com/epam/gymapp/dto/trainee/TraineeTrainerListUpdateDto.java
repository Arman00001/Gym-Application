package com.epam.gymapp.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TraineeTrainerListUpdateDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotEmpty
    private List<String> trainerUsernames;
}
