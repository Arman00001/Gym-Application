package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;

    @Override
    public TrainingDto createTraining(TrainingCreateDto trainingCreateDto) {
        log.info("Creating training profile for {} by {}",
                trainingCreateDto.getTraineeUsername(),
                trainingCreateDto.getTrainerUsername());

        Training training = trainingRepository.save(TrainingMapper.mapCreateToTraining(trainingCreateDto));

        log.info("Training profile created successfully. Trainee username={}, trainer username = {}",
                training.getTraineeUsername(),
                training.getTrainerUsername()
        );

        return TrainingMapper.mapToDto(training);
    }

    @Override
    public TrainingDto getTraining(Long id) {
        log.info("Getting training profile. Training id={}", id);

        Training training = trainingRepository.get(id);
        if (training == null) {
            log.warn("Training profile not found. Training id={}", id);
            throw new IllegalArgumentException("Training does not exist");
        }

        return TrainingMapper.mapToDto(training);
    }
}
