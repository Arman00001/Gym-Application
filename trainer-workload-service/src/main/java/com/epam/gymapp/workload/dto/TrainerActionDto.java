package com.epam.gymapp.workload.dto;

import com.epam.gymapp.workload.persistence.entity.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerActionDto {
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "first name is required")
    private String firstName;
    @NotBlank(message = "last name is required")
    private String lastName;
    @NotNull(message = "activity status is required")
    private Boolean isActive;
    @NotNull(message = "training date is required")
    private LocalDate trainingDate;
    @NotNull(message = "duration should be non-empty and positive")
    @Positive
    private Long duration;
    @NotNull(message = "action type is required")
    private ActionType actionType;
}
