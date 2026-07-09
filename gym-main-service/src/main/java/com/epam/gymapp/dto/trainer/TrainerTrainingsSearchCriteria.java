package com.epam.gymapp.dto.trainer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerTrainingsSearchCriteria {
    private LocalDate fromDate;
    private LocalDate toDate;

    private String traineeFirstName;
    private String traineeLastName;
}