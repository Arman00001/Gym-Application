package com.epam.gymapp.workload;

import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;

public record TrainingWorkloadEvent(TrainerActionDto dto, String transactionId) {
}
