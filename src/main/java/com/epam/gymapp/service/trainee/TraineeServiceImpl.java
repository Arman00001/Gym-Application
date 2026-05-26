package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.mapper.TraineeMapper;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeRepository traineeRepository;
    private UserService userService;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto) {
        log.info("Creating trainee profile for {} {}",
                traineeCreateDto.getFirstName(),
                traineeCreateDto.getLastName());

        User user = userService.createUser(UserMapper.INSTANCE.traineeToCreateUser(traineeCreateDto));

        Trainee trainee = TraineeMapper.INSTANCE.mapCreateToTrainee(traineeCreateDto);
        trainee.setUser(user);

        traineeRepository.save(trainee);

        log.info("Trainee profile created successfully. username={}", user.getUsername());

        return TraineeMapper.INSTANCE.mapToCreateResponse(user);
    }

    @Override
    public TraineeDto updateTrainee(TraineeUpdateDto dto) {
        log.info("Updating trainee profile. username={}", dto.getUsername());

        Trainee existing = traineeRepository.getByUsername(dto.getUsername()).orElseThrow(()->{
            log.warn("Cannot update trainee. Trainee not found. username={}", dto.getUsername());
            return new IllegalArgumentException("Trainee does not exist");
        });

        User user = existing.getUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsActive(dto.getIsActive());

        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setAddress(dto.getAddress());

        Trainee updated = traineeRepository.update(existing);

        log.info("Trainee profile updated successfully. username={}", user.getUsername());

        return TraineeMapper.INSTANCE.mapToDto(updated, user);
    }

    @Override
    public void deleteTrainee(Long id) {
        log.info("Deleting trainee profile. id={}", id);
        traineeRepository.delete(id);
        log.info("Trainee profile deleted. id={}", id);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee profile. username={}", username);
        traineeRepository.deleteByUsername(username);
    }

    @Override
    public TraineeDto getTraineeById(Long id) {
        log.info("Getting trainee profile. id={}", id);

        Trainee trainee = traineeRepository.get(id).orElseThrow(() -> {
            log.warn("Trainee profile not found. id={}", id);
            return new IllegalArgumentException("Trainee does not exist");
        });

        return TraineeMapper.INSTANCE.mapToDto(trainee, trainee.getUser());
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        log.info("Getting trainee profile. username={}", username);
        Trainee trainee = traineeRepository.getByUsername(username).orElseThrow(()->{
            log.warn("Trainee profile not found. username={}", username);
            return new IllegalArgumentException("Trainee does not exist");
        });

        return TraineeMapper.INSTANCE.mapToDto(trainee, trainee.getUser());
    }
}
