package com.epam.gymapp.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeTrainingsSearchCriteria {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String trainerFirstName;
    private String trainerLastName;
    private String trainingType;
}
