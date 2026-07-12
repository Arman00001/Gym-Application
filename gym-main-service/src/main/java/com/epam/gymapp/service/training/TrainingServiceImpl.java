package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.trainer.workload.ActionType;
import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;
import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.logging.TransactionConstants;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import com.epam.gymapp.workload.TrainingWorkloadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link TrainingService}.
 *
 * <p>
 * This implementation manages training persistence using {@link TrainingRepository}.
 * During training creation and deletion, it publishes trainer workload events
 * that are later sent to the workload microservice.
 * </p>
 */
@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingRepository trainingRepository;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private ApplicationEventPublisher applicationEventPublisher;

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

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public TrainingDto createTraining(TrainingCreateDto trainingCreateDto) {
        log.info("Creating training profile for trainee {} by trainer {}",
                trainingCreateDto.getTraineeUsername(),
                trainingCreateDto.getTrainerUsername());
        Trainee trainee = traineeRepository.getByUsername(trainingCreateDto.getTraineeUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Trainee does not exist"));
        Trainer trainer = trainerRepository.getByUsername(trainingCreateDto.getTrainerUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer does not exist"));


        Training training = TrainingMapper.INSTANCE.mapCreateToTraining(trainingCreateDto);

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        trainingRepository.save(training);

        User trainerUser = trainer.getUser();

        TrainerActionDto dto = constructTrainerAction(
                training,
                trainerUser,
                ActionType.ADD
        );

        String transactionId = Optional
                .ofNullable(MDC.get(TransactionConstants.TRANSACTION_ID))
                .orElse(UUID.randomUUID().toString());


        applicationEventPublisher.publishEvent(new TrainingWorkloadEvent(dto, transactionId));

        log.info("Training profile created successfully. Trainee id={}, trainer id = {}",
                trainee.getId(),
                trainer.getId()
        );

        return TrainingMapper.INSTANCE.mapToDto(training);
    }

    @Override
    public TrainingDto getTraining(Long id) {
        log.info("Getting training profile. Training id={}", id);

        Training training = trainingRepository.findById(id).orElseThrow(() -> {
            log.warn("Training profile not found. Training id={}", id);
            return new ResourceNotFoundException("Training does not exist");
        });

        return TrainingMapper.INSTANCE.mapToDto(training);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting training \nid: {}", id);
        Training training = trainingRepository.deleteTrainingById(id);
        TrainerActionDto dto = constructTrainerAction(training, training.getTrainer().getUser(), ActionType.DELETE);

        String transactionId = Optional
                .ofNullable(MDC.get(TransactionConstants.TRANSACTION_ID))
                .orElse(UUID.randomUUID().toString());


        applicationEventPublisher.publishEvent(new TrainingWorkloadEvent(dto, transactionId));
        log.info("Training successfully deleted \nid: {}", id);
    }

    /**
     * Creates a trainer workload action DTO from the given training and trainer user.
     *
     * @param training   the training used to provide the date and duration
     * @param user       the trainer user whose workload should be updated
     * @param actionType the type of workload action to perform
     * @return the trainer workload action DTO
     */
    private TrainerActionDto constructTrainerAction(
            Training training,
            User user,
            ActionType actionType
    ) {
        TrainerActionDto dto = new TrainerActionDto();
        dto.setActionType(actionType);
        dto.setDuration(training.getDuration());
        dto.setTrainingDate(training.getDate());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setIsActive(user.getIsActive());

        return dto;
    }
}
