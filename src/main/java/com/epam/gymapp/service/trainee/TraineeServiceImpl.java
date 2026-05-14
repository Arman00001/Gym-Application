package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.mapper.TraineeMapper;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.util.PasswordGenerator;
import com.epam.gymapp.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    public TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto) {
        log.info("Creating trainee profile for {} {}",
                traineeCreateDto.getFirstName(),
                traineeCreateDto.getLastName());

        Trainee trainee = TraineeMapper.mapCreateToTrainee(traineeCreateDto);
        String username = usernameGenerator.generate(trainee.getFirstName(),trainee.getLastName());
        String password = passwordGenerator.generate();
        trainee.setUsername(username);
        trainee.setPassword(password);

        Trainee traineeResult = traineeRepository.save(trainee);

        log.info("Trainee profile created successfully. username={}", traineeResult.getUsername());

        return TraineeMapper.mapToCreatedDto(traineeResult);
    }

    @Override
    public TraineeDto updateTrainee(TraineeUpdateDto dto) {
        log.info("Updating trainee profile. username={}", dto.getUsername());
        Trainee prev = traineeRepository.get(dto.getUsername());

        if (prev == null) {
            log.warn("Cannot update trainee. Trainee not found. username={}", dto.getUsername());
            throw new IllegalArgumentException("Trainee does not exist");
        }

        Trainee trainee = TraineeMapper.mapUpdateToTrainee(dto);

        trainee.setId(prev.getId());
        trainee.setUserId(prev.getUserId());
        trainee.setPassword(prev.getPassword());

        Trainee updated = traineeRepository.update(trainee);
        log.info("Trainee profile updated successfully. username={}", updated.getUsername());

        return TraineeMapper.mapToDto(updated);
    }

    @Override
    public void deleteTrainee(String username) {
        log.info("Deleting trainee profile. username={}", username);
        traineeRepository.delete(username);
        log.info("Trainee profile deleted. username={}", username);
    }

    @Override
    public TraineeDto getTrainee(String username) {
        log.info("Getting trainee profile. username={}", username);

        Trainee trainee = traineeRepository.get(username);
        if (trainee == null) {
            log.warn("Trainee profile not found. username={}", username);
            throw new IllegalArgumentException("Trainee does not exist");
        }

        return TraineeMapper.mapToDto(trainee);
    }
}
