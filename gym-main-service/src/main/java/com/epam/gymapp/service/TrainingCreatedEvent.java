package com.epam.gymapp.service;

import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;

public record TrainingCreatedEvent(TrainerActionDto dto) {}
