package com.epam.gymapp.dto.trainee;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeTrainingsSearchCriteria {
    private LocalDate fromDate;
    private LocalDate toDate;

    private String trainerFirstName;
    private String trainerLastName;
    private String trainingType;
}
