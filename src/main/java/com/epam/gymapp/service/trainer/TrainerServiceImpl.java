package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.mapper.TrainerMapper;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.util.PasswordGenerator;
import com.epam.gymapp.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    public TrainerCreateResponse createTrainer(TrainerCreateDto dto) {
        log.info("Creating trainer profile for {} {}",
                dto.getFirstName(),
                dto.getLastName());

        Trainer trainer = TrainerMapper.mapToTrainer(dto);
        trainer.setIsActive(true);
        String username = usernameGenerator.generate(trainer.getFirstName(),trainer.getLastName());
        String password = passwordGenerator.generate();

        trainer.setUsername(username);
        trainer.setPassword(password);

        Trainer trainerResponse = trainerRepository.save(trainer);
        log.info("Trainee profile created successfully. username={}", trainerResponse.getUsername());

        return TrainerMapper.mapToCreatedDto(trainerResponse);
    }

    @Override
    public TrainerDto updateTrainer(TrainerUpdateDto dto) {
        log.info("Updating trainer profile. username={}", dto.getUsername());
        Trainer prev = trainerRepository.get(dto.getUsername());
        if (prev == null) {
            log.warn("Cannot update trainer. Trainer not found. username={}", dto.getUsername());
            throw new IllegalArgumentException("Trainer does not exist");
        }

        Trainer trainer = TrainerMapper.mapUpdateToTrainer(dto);

        trainer.setId(prev.getId());
        trainer.setUserId(prev.getUserId());
        trainer.setPassword(prev.getPassword());

        Trainer updated = trainerRepository.update(trainer);
        log.info("Trainer profile updated successfully. username={}", updated.getUsername());

        return TrainerMapper.mapToDto(updated);
    }

    @Override
    public TrainerDto getTrainer(String username) {
        log.info("Getting trainer profile. username={}", username);

        Trainer trainer = trainerRepository.get(username);
        if (trainer == null) {
            log.warn("Trainer profile not found. username={}", username);
            throw new IllegalArgumentException("Trainer does not exist");
        }

        return TrainerMapper.mapToDto(trainer);
    }
}