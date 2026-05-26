package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.mapper.TrainerMapper;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import com.epam.gymapp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerRepository trainerRepository;
    private UserService userService;
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainingTypeRepositoryImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public TrainerCreateResponse createTrainer(TrainerCreateDto dto) {
        log.info("Creating trainer profile for {} {}",
                dto.getFirstName(),
                dto.getLastName());
        TrainingType trainingType = trainingTypeRepository.getByName(dto.getSpecialization()).orElseThrow(() -> {
            log.warn("Specialization not found. name={}",dto.getSpecialization());
            return new IllegalArgumentException("Specialization not found");
        });

        User user = userService.createUser(UserMapper.INSTANCE.trainerToCreateUser(dto));

        Trainer trainer = TrainerMapper.INSTANCE.mapToTrainer(dto);
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        trainerRepository.save(trainer);
        log.info("Trainer profile created successfully. username={}", user.getUsername());

        return TrainerMapper.INSTANCE.mapToCreateResponse(user);
    }

    @Override
    public TrainerDto updateTrainer(TrainerUpdateDto dto) {
        log.info("Updating trainer profile. username={}", dto.getUsername());

        Trainer existing = trainerRepository.getByUsername(dto.getUsername()).orElseThrow(()->{
            log.warn("Cannot update trainer. Trainer not found. username={}", dto.getUsername());
            return new IllegalArgumentException("Trainer does not exist");
        });

        User user = existing.getUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsActive(dto.getIsActive());

        TrainingType specialization = trainingTypeRepository.getByName(dto.getSpecialization()).orElseThrow(() -> {
            log.warn("Specialization not found. name={}",dto.getSpecialization());
            return new IllegalArgumentException("Specialization not found");
        });

        existing.setSpecialization(specialization);

        Trainer updatedTrainer = trainerRepository.update(existing);

        log.info("Trainer profile updated successfully. username={}", user.getUsername());

        return TrainerMapper.INSTANCE.mapToDto(updatedTrainer, user, specialization);
    }

    @Override
    public TrainerDto getTrainerById(Long id) {
        log.info("Getting trainer profile. id={}", id);

        Trainer trainer = trainerRepository.get(id).orElseThrow(() -> {
            log.warn("Trainer profile not found. id={}", id);
            return new IllegalArgumentException("Trainer does not exist");
        });

        return TrainerMapper.INSTANCE.mapToDto(trainer, trainer.getUser(), trainer.getSpecialization());
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        log.info("Getting trainer profile. username={}", username);
        Trainer trainer = trainerRepository.getByUsername(username).orElseThrow(() -> {
            log.warn("Trainer profile not found. username={}", username);
            return new IllegalArgumentException("Trainer does not exist");
        });

        return TrainerMapper.INSTANCE.mapToDto(trainer, trainer.getUser(), trainer.getSpecialization());
    }
}