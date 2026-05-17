package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.dto.user.UserUpdateDto;
import com.epam.gymapp.mapper.TrainerMapper;
import com.epam.gymapp.mapper.UserMapper;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
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

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
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
        User user = userService.createUser(UserMapper.INSTANCE.trainerToCreateUser(dto));

        Trainer trainer = TrainerMapper.INSTANCE.mapToTrainer(dto);
        trainer.setUserId(user.getId());

        trainerRepository.save(trainer);
        log.info("Trainer profile created successfully. username={}", user.getUsername());

        TrainerCreateResponse response = new TrainerCreateResponse();
        response.setUsername(user.getUsername());
        response.setPassword(user.getPassword());

        return response;
    }

    @Override
    public TrainerDto updateTrainer(TrainerUpdateDto dto) {
        log.info("Updating trainer profile. username={}", dto.getUsername());

        User user = userService.getByUsername(dto.getUsername());
        UserUpdateDto userDto = UserMapper.INSTANCE.trainerToUpdateUser(dto);
        userDto.setId(user.getId());

        var updatedUser = userService.updateUser(userDto);
        if(updatedUser == null){
            log.warn("Cannot update trainer. User not found. username={}", dto.getUsername());
            throw new IllegalArgumentException("Trainer does not exist");
        }

        Trainer existing = trainerRepository.getByUserId(updatedUser.getId());

        if (existing == null) {
            log.warn("Cannot update trainer. Trainer not found. username={}", updatedUser.getUsername());
            throw new IllegalArgumentException("Trainer does not exist");
        }

        existing.setSpecialization(dto.getSpecialization());

        Trainer updatedTrainer = trainerRepository.update(existing);

        log.info("Trainer profile updated successfully. username={}", updatedUser.getUsername());

        return TrainerMapper.INSTANCE.mapToDto(updatedTrainer, updatedUser);
    }

    @Override
    public TrainerDto getTrainerById(Long id) {
        log.info("Getting trainer profile. id={}", id);

        Trainer trainer = trainerRepository.get(id);
        if (trainer == null) {
            log.warn("Trainer profile not found. id={}", id);
            throw new IllegalArgumentException("Trainer does not exist");
        }
        User user = userService.getById(trainer.getUserId());

        return TrainerMapper.INSTANCE.mapToDto(trainer, user);
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        log.info("Getting trainer profile. username={}", username);
        User user = userService.getByUsername(username);

        Trainer trainer = trainerRepository.getByUserId(user.getId());
        if (trainer == null) {
            log.warn("Trainer profile not found. username={}", username);
            throw new IllegalArgumentException("Trainer does not exist");
        }

        return TrainerMapper.INSTANCE.mapToDto(trainer, user);
    }
}