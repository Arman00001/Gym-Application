package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;

public interface TrainerService {
    TrainerCreateResponse createTrainer(TrainerCreateDto trainerCreateDto);
    TrainerDto updateTrainer(TrainerUpdateDto trainerDto);
    TrainerDto getTrainer(String username);
}
