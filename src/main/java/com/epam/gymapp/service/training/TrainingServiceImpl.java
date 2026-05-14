package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingRepository trainingRepository;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }
    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }
    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public TrainingDto createTraining(TrainingCreateDto trainingCreateDto) {
        log.info("Creating training profile for {} by {}",
                trainingCreateDto.getTraineeUsername(),
                trainingCreateDto.getTrainerUsername());

        if (traineeRepository.get(trainingCreateDto.getTraineeUsername()) == null) {
            throw new IllegalArgumentException("Trainee does not exist");
        }

        if (trainerRepository.get(trainingCreateDto.getTrainerUsername()) == null) {
            throw new IllegalArgumentException("Trainer does not exist");
        }
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
