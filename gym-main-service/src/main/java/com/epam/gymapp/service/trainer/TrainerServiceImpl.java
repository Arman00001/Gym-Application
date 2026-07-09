package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.*;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.dto.user.CreatedUserResult;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.mapper.TrainerMapper;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.*;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import com.epam.gymapp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link TrainerService}.
 *
 * <p>
 * This implementation manages trainer profiles using {@link TrainerRepository}.
 * It creates the associated user account through {@link UserService}, resolves
 * trainer specializations through {@link TrainingTypeRepository}, and maps trainer
 * entities to DTOs using {@link TrainerMapper}.
 * </p>
 */
@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerRepository trainerRepository;
    private TraineeRepository traineeRepository;
    private UserService userService;
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    @Transactional
    public TrainerCreateResponse createTrainer(TrainerCreateDto dto) {
        log.info("Creating trainer profile for {} {}",
                dto.getFirstName(),
                dto.getLastName());
        TrainingType trainingType = trainingTypeRepository.findByName(dto.getSpecialization()).orElseThrow(() -> {
            log.warn("Specialization not found. name={}", dto.getSpecialization());
            return new ResourceNotFoundException("Specialization not found");
        });

        CreatedUserResult createdUser = userService.createUser(UserMapper.INSTANCE.trainerToCreateUser(dto), Role.TRAINER);
        User user = createdUser.user();

        Trainer trainer = TrainerMapper.INSTANCE.mapToTrainer(dto);
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        trainerRepository.save(trainer);
        log.info("Trainer profile created successfully. username={}", user.getUsername());

        return TrainerMapper.INSTANCE.mapToCreateResponse(user, createdUser.rawPassword());
    }

    @Override
    @Transactional
    public TrainerDto updateTrainer(String username, TrainerUpdateDto dto) {
        log.info("Updating trainer profile. username={}", username);
        Trainer existing = trainerRepository.getByUsername(username).orElseThrow(() -> {
            log.warn("Cannot update trainer. Trainer not found. username={}", username);
            return new ResourceNotFoundException("Trainer does not exist");
        });

        User user = existing.getUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsActive(dto.getIsActive());

        TrainingType specialization = trainingTypeRepository.findByName(dto.getSpecialization()).orElseThrow(() -> {
            log.warn("Specialization not found. name={}", dto.getSpecialization());
            return new ResourceNotFoundException("Specialization not found");
        });

        existing.setSpecialization(specialization);

        Trainer updatedTrainer = trainerRepository.save(existing);
        List<Trainee> trainees = traineeRepository.getAllByTrainerUsername(user.getUsername());

        log.info("Trainer profile updated successfully. username={}", user.getUsername());

        return TrainerMapper.INSTANCE.mapToFullDto(updatedTrainer, trainees);
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        log.info("Getting trainer profile. username={}", username);
        Trainer trainer = trainerRepository.getByUsername(username).orElseThrow(() -> {
            log.warn("Trainer profile not found. username={}", username);
            return new ResourceNotFoundException("Trainer does not exist");
        });

        List<Trainee> trainees = traineeRepository.getAllByTrainerUsername(trainer.getUser().getUsername());

        return TrainerMapper.INSTANCE.mapToFullDto(trainer, trainees);
    }

    @Override
    public List<TrainerDto> getNotAssignedToTrainee(String username) {
        log.info("Getting trainers not assigned to trainee with username = {}", username);
        List<Trainer> trainers = trainerRepository.getNotAssignedToTrainee(username);

        return TrainerMapper.INSTANCE.mapToDtoList(trainers);
    }

    @Override
    public List<TrainingDto> searchTrainings(TrainerTrainingsSearchCriteria criteria, String username) {
        log.info("Searching trainer trainings using criteria");
        return TrainingMapper.INSTANCE.mapToDtoList(trainerRepository.getTrainingsByCriteria(criteria, username));
    }

    @Override
    @Transactional
    public TrainerDto changeIsActiveStatus(String username) {
        log.info("Changing active status for: username = {}", username);
        Trainer trainer = trainerRepository.getByUsername(username).orElseThrow(() -> {
            log.warn("Cannot update trainer. Trainer not found. username={}", username);
            return new ResourceNotFoundException("Trainer does not exist");
        });
        User user = trainer.getUser();
        user.setIsActive(!user.getIsActive());
        trainerRepository.save(trainer);

        return TrainerMapper.INSTANCE.mapToDto(trainer, trainer.getUser(), trainer.getSpecialization());
    }
}