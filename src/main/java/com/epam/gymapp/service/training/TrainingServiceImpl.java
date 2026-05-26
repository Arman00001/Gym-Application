package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
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
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public TrainingDto createTraining(TrainingCreateDto trainingCreateDto) {
        log.info("Creating training profile for trainee {} by trainer {}",
                trainingCreateDto.getTraineeUsername(),
                trainingCreateDto.getTrainerUsername());
        Trainee trainee = traineeRepository.getByUsername(trainingCreateDto.getTraineeUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainee does not exist"));
        Trainer trainer = trainerRepository.getByUsername(trainingCreateDto.getTrainerUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainer does not exist"));


        Training training = TrainingMapper.INSTANCE.mapCreateToTraining(trainingCreateDto);

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        trainingRepository.save(training);

        log.info("Training profile created successfully. Trainee id={}, trainer id = {}",
                trainee.getId(),
                trainer.getId()
        );

        return TrainingMapper.INSTANCE.mapToDto(training);
    }

    @Override
    public TrainingDto getTraining(Long id) {
        log.info("Getting training profile. Training id={}", id);

        Training training = trainingRepository.get(id).orElseThrow(() -> {
            log.warn("Training profile not found. Training id={}", id);
            return new IllegalArgumentException("Training does not exist");
        });

        return TrainingMapper.INSTANCE.mapToDto(training);
    }
}
