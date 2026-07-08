package com.epam.gymapp.workload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerWorkloadDto {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean status;
    private Integer year;
    private Integer month;
    private Long trainingSummaryDuration;
}
