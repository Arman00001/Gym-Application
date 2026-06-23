package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.exception.BadCredentialsException;
import com.epam.gymapp.exception.ResourceNotFoundException;
import com.epam.gymapp.mapper.TraineeMapper;
import com.epam.gymapp.mapper.TrainingMapper;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.service.user.UserService;
import jakarta.persistence.EntityManager;
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

        User user = userService.createUser(UserMapper.INSTANCE.traineeToCreateUser(traineeCreateDto));

        Trainee trainee = TraineeMapper.INSTANCE.mapCreateToTrainee(traineeCreateDto);
        trainee.setUser(user);

        traineeRepository.save(trainee);

        log.info("Trainee profile created successfully. username={}", user.getUsername());

        return TraineeMapper.INSTANCE.mapToCreateResponse(user);
    }

    @Override
    @Transactional
    public TraineeDto updateTrainee(TraineeUpdateDto dto) {
        log.info("Updating trainee profile. username={}", dto.getUsername());
        if (userService.isAuthenticated(dto.getUsername(), dto.getPassword())) {
            Trainee existing = traineeRepository.getByUsername(dto.getUsername()).orElseThrow(() -> {
                log.warn("Cannot update trainee. Trainee not found. username={}", dto.getUsername());
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

        throw new BadCredentialsException("Incorrect Credentials");
    }

//    @Override
//    @Transactional
//    public TraineeDto updateTrainerList(TraineeTrainerListUpdateDto dto) {
//        EntityTransaction transaction = entityManager.getTransaction();
//        try {
//            transaction.begin();
//            if (userService.isAuthenticated(dto.getUsername(), dto.getPassword())) {
//                Trainee trainee = traineeRepository.getByUsername(dto.getUsername()).orElseThrow(() -> {
//                    log.warn("Cannot update trainee. Trainee not found. username={}", dto.getUsername());
//                    return new ResourceNotFoundException("Trainee does not exist");
//                });
//                List<Trainer> trainers = trainerRepository.getByUsernames(dto.getTrainerUsernames());
//                traineeTrainerRepository.updateTrainerList(trainee, trainers);
//                transaction.commit();
//
//                return TraineeMapper.INSTANCE.mapToFullDto(trainee, trainers);
//            }
//
//            throw new BadCredentialsException("Incorrect Credentials");
//        } catch (Exception e){
//            if(transaction.isActive()){
//                transaction.rollback();
//            }
//            throw e;
//        }
//    }

    @Override
    @Transactional
    public void deleteTrainee(DeleteRequestDto dto) {
        log.info("Deleting trainee profile. id={}", dto.getId());
        if (userService.isAuthenticated(dto.getUsername(), dto.getPassword())) {
            traineeRepository.deleteById(dto.getId());
            log.info("Trainee profile deleted. id={}", dto.getId());
            return;
        }

        throw new BadCredentialsException("Incorrect Credentials");

    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(DeleteRequestDto dto) {
        log.info("Deleting trainee profile. username={}", dto.getUsername());
        if (userService.isAuthenticated(dto.getUsername(), dto.getPassword())) {
            traineeRepository.deleteByUsername(dto.getUsername());

            log.info("Trainee profile deleted. username={}", dto.getUsername());
            return;
        }

        throw new BadCredentialsException("Incorrect Credentials");
    }

    @Override
    public TraineeDto getTraineeById(Long id) {
        log.info("Getting trainee profile. id={}", id);

        Trainee trainee = traineeRepository.findById(id).orElseThrow(() -> {
            log.warn("Trainee profile not found. id={}", id);
            return new ResourceNotFoundException("Trainee does not exist");
        });
        List<Trainer> trainers = trainerRepository.getAllByTraineeUsername(trainee.getUser().getUsername());


        return TraineeMapper.INSTANCE.mapToFullDto(trainee, trainers);
    }

    @Override
    public TraineeDto getTraineeByUsername(AuthenticationRequestDto dto) {
        log.info("Getting trainee profile. username={}", dto.getUsername());
        if (userService.isAuthenticated(dto.getUsername(), dto.getPassword())) {
            Trainee trainee = traineeRepository.getByUsername(dto.getUsername()).orElseThrow(() -> {
                log.warn("Trainee profile not found. username={}", dto.getUsername());
                return new ResourceNotFoundException("Trainee does not exist");
            });
            List<Trainer> trainers = trainerRepository.getAllByTraineeUsername(trainee.getUser().getUsername());

            return TraineeMapper.INSTANCE.mapToFullDto(trainee, trainers);
        }

        throw new BadCredentialsException("Incorrect Credentials");
    }

    @Override
    public List<TrainingDto> searchTrainings(TraineeTrainingsSearchCriteria criteria) {
        if (userService.isAuthenticated(criteria.getUsername(), criteria.getPassword())) {
            return TrainingMapper.INSTANCE.mapToDtoList(traineeRepository.getTrainingsByCriteria(criteria));
        }

        throw new BadCredentialsException("Incorrect Credentials");
    }

    @Override
    @Transactional
    public TraineeDto changeIsActiveStatus(AuthenticationRequestDto auth) {
        log.info("Changing active status for: username = {}", auth.getUsername());
        if (userService.isAuthenticated(auth.getUsername(), auth.getPassword())) {
            Trainee trainee = traineeRepository.getByUsername(auth.getUsername()).orElseThrow(() -> {
                log.warn("Cannot update trainee. Trainee not found. username={}", auth.getUsername());
                return new ResourceNotFoundException("Trainee does not exist");
            });;
            User user = trainee.getUser();
            user.setIsActive(!user.getIsActive());
            traineeRepository.save(trainee);

            return TraineeMapper.INSTANCE.mapToDto(trainee, trainee.getUser());
        }

        throw new BadCredentialsException("Incorrect Credentials");
    }
}
