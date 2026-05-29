package com.epam.gymapp.dto.training;

import com.epam.gymapp.persistence.entity.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingCreateDto {
    @NotBlank
    private String traineeUsername;
    @NotBlank
    private String trainerUsername;
    @NotBlank
    private String name;
    @NotNull
    private LocalDate date;
    @NotNull
    private TrainingType type;
    @NotNull
    private Long duration;
}