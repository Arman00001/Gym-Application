package com.epam.gymapp.workload.service;

import com.epam.gymapp.workload.dto.TrainerActionDto;
import com.epam.gymapp.workload.dto.TrainerWorkloadDto;

public interface TrainerWorkloadService {
    void updateTrainerWorkload(TrainerActionDto trainerActionDto);

    TrainerWorkloadDto getTrainerWorkload(String username, Integer year, Integer month);
}
