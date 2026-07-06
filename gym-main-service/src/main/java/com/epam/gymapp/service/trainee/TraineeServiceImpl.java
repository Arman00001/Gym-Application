package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.dto.user.CreatedUserResult;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.mapper.TraineeMapper;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.Role;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeRepository traineeRepository;
    private UserService userService;
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
    void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto) {
        log.info("Creating trainee profile for {} {}",
                traineeCreateDto.getFirstName(),
                traineeCreateDto.getLastName());

        CreatedUserResult createdUser = userService.createUser(UserMapper.INSTANCE.traineeToCreateUser(traineeCreateDto), Role.TRAINEE);
        User user = createdUser.user();

        Trainee trainee = TraineeMapper.INSTANCE.mapCreateToTrainee(traineeCreateDto);
        trainee.setUser(user);

        traineeRepository.save(trainee);

        log.info("Trainee profile created successfully. username={}", user.getUsername());

        return TraineeMapper.INSTANCE.mapToCreateResponse(user, createdUser.rawPassword());
    }

    @Override
    @Transactional
    public TraineeDto updateTrainee(String username, TraineeUpdateDto dto) {
        log.info("Updating trainee profile. username={}", username);
        Trainee existing = traineeRepository.getByUsername(username).orElseThrow(() -> {
            log.warn("Cannot update trainee. Trainee not found. username={}", username);
            return new ResourceNotFoundException("Trainee does not exist");
        });

        User user = existing.getUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsActive(dto.getIsActive());

        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setAddress(dto.getAddress());

        Trainee updated = traineeRepository.save(existing);
        List<Trainer> trainers = trainerRepository.getAllByTraineeUsername(updated.getUser().getUsername());


        log.info("Trainee profile updated successfully. username={}", user.getUsername());

        return TraineeMapper.INSTANCE.mapToFullDto(updated, trainers);
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(DeleteRequestDto dto) {
        log.info("Deleting trainee profile. username={}", dto.getUsername());
        traineeRepository.deleteByUsername(dto.getUsername());

        log.info("Trainee profile deleted. username={}", dto.getUsername());
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        log.info("Getting trainee profile. username={}", username);
        Trainee trainee = traineeRepository.getByUsername(username).orElseThrow(() -> {
            log.warn("Trainee profile not found. username={}", username);
            return new ResourceNotFoundException("Trainee does not exist");
        });
        List<Trainer> trainers = trainerRepository.getAllByTraineeUsername(trainee.getUser().getUsername());

        return TraineeMapper.INSTANCE.mapToFullDto(trainee, trainers);
    }

    @Override
    public List<TrainingDto> searchTrainings(TraineeTrainingsSearchCriteria criteria) {
        return TrainingMapper.INSTANCE.mapToDtoList(traineeRepository.getTrainingsByCriteria(criteria));
    }

    @Override
    @Transactional
    public TraineeDto changeIsActiveStatus(String username) {
        log.info("Changing active status for: username = {}", username);
        Trainee trainee = traineeRepository.getByUsername(username).orElseThrow(() -> {
            log.warn("Cannot update trainee. Trainee not found. username={}", username);
            return new ResourceNotFoundException("Trainee does not exist");
        });
        User user = trainee.getUser();
        user.setIsActive(!user.getIsActive());
        traineeRepository.save(trainee);

        return TraineeMapper.INSTANCE.mapToDto(trainee, trainee.getUser());
    }
}
