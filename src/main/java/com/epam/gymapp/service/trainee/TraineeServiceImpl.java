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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    public TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto) {
        Trainee trainee = TraineeMapper.mapCreateToTrainee(traineeCreateDto);
        String username = usernameGenerator.generate(trainee.getFirstName(),trainee.getLastName());
        String password = passwordGenerator.generate();
        trainee.setUsername(username);
        trainee.setPassword(password);

        Trainee traineeResult = traineeRepository.save(trainee);

        return TraineeMapper.mapToCreatedDto(traineeResult);
    }

    @Override
    public TraineeDto updateTrainee(TraineeUpdateDto dto) {
        Trainee trainee = TraineeMapper.mapUpdateToTrainee(dto);

        return TraineeMapper.mapToDto(traineeRepository.update(trainee));
    }

    @Override
    public void deleteTrainee(String username) {
        traineeRepository.delete(username);
    }

    @Override
    public TraineeDto getTrainee(String username) {
        Trainee trainee = traineeRepository.get(username);
        return TraineeMapper.mapToDto(trainee);
    }
}
