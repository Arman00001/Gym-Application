package com.epam.gymapp.dto.trainer.workload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerActionDto {
    @NotBlank
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private Boolean isActive;
    @NotNull
    private LocalDate trainingDate;
    @NotNull
    @Positive
    private Long duration;
    @NotNull
    private ActionType actionType;
}
