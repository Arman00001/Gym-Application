package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
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

    @Autowired void setUserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto) {
        log.info("Creating trainee profile for {} {}",
                traineeCreateDto.getFirstName(),
                traineeCreateDto.getLastName());

        User user = userService.createUser(UserMapper.INSTANCE.traineeToCreateUser(traineeCreateDto));

        Trainee trainee = TraineeMapper.INSTANCE.mapCreateToTrainee(traineeCreateDto);
        trainee.setUserId(user.getId());

        traineeRepository.save(trainee);

        log.info("Trainee profile created successfully. username={}", user.getUsername());

        TraineeCreateResponse response = new TraineeCreateResponse();
        response.setUsername(user.getUsername());
        response.setPassword(user.getPassword());

        return response;
    }

    @Override
    public TraineeDto updateTrainee(TraineeUpdateDto dto) {
        log.info("Updating trainee profile. username={}", dto.getUsername());

        User user = userService.getByUsername(dto.getUsername());
        UserUpdateDto userDto = UserMapper.INSTANCE.traineeToUpdateUser(dto);
        userDto.setId(user.getId());

        var updatedUser = userService.updateUser(userDto);
        if(updatedUser == null){
            log.warn("Cannot update user profile. User not found. username={}",dto.getUsername());
            throw new IllegalArgumentException("Trainee does not exist");
        }

        Trainee existing = traineeRepository.getByUserId(updatedUser.getId());

        if (existing == null) {
            log.warn("Cannot update trainee. Trainee not found. username={}", updatedUser.getUsername());
            throw new IllegalArgumentException("Trainee does not exist");
        }

        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setAddress(dto.getAddress());

        Trainee updated = traineeRepository.update(existing);

        log.info("Trainee profile updated successfully. username={}", updatedUser.getUsername());

        return TraineeMapper.INSTANCE.mapToDto(updated, updatedUser);
    }

    @Override
    public void deleteTrainee(Long id) {
        log.info("Deleting trainee profile. id={}", id);
        Trainee trainee = traineeRepository.delete(id);
        userService.deleteUser(trainee.getUserId());

        log.info("Trainee profile deleted. id={}", id);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee profile. username={}", username);
        User user = userService.getByUsername(username);
        if(user==null){
            log.warn("User not found. username={}",username);
            throw new IllegalArgumentException("Trainee does not exist");
        }

        traineeRepository.deleteByUserId(user.getId());
        userService.deleteUser(user.getId());
        log.info("Trainee deleted. username={}",username);
    }

    @Override
    public TraineeDto getTraineeById(Long id) {
        log.info("Getting trainee profile. id={}", id);

        Trainee trainee = traineeRepository.get(id);
        if (trainee == null) {
            log.warn("Trainee profile not found. id={}", id);
            throw new IllegalArgumentException("Trainee does not exist");
        }
        User user = userService.getById(trainee.getUserId());

        return TraineeMapper.INSTANCE.mapToDto(trainee, user);
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        log.info("Getting trainee profile. username={}", username);
        User user = userService.getByUsername(username);

        Trainee trainee = traineeRepository.getByUserId(user.getId());
        if (trainee == null) {
            log.warn("Trainee profile not found. username={}", username);
            throw new IllegalArgumentException("Trainee does not exist");
        }

        return TraineeMapper.INSTANCE.mapToDto(trainee, user);
    }
}
