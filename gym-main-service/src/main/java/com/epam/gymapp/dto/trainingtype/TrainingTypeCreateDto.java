package com.epam.gymapp.dto.trainingtype;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingTypeCreateDto {
    @NotBlank
    private String name;
}
