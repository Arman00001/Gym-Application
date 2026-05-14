package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;

    @Override
    public TrainingDto createTraining(TrainingCreateDto trainingCreateDto) {
        Training training = trainingRepository.save(TrainingMapper.mapCreateToTraining(trainingCreateDto));

        return TrainingMapper.mapToDto(training);
    }

    @Override
    public TrainingDto getTraining(String trainerUsername) {
        return TrainingMapper.mapToDto(trainingRepository.get(trainerUsername));
    }
}
