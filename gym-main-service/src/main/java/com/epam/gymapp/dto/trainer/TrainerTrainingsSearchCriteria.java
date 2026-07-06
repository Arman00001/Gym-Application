package com.epam.gymapp.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerTrainingsSearchCriteria {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String traineeFirstName;
    private String traineeLastName;
}