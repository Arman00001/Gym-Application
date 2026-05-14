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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    public TrainerCreateResponse createTrainer(TrainerCreateDto dto) {
        Trainer trainer = TrainerMapper.mapToTrainer(dto);
        trainer.setIsActive(true);
        String username = usernameGenerator.generate(trainer.getFirstName(),trainer.getLastName());
        String password = passwordGenerator.generate();

        trainer.setUsername(username);
        trainer.setPassword(password);

        Trainer trainerResponse = trainerRepository.save(trainer);
        return TrainerMapper.mapToCreatedDto(trainerResponse);
    }

    @Override
    public TrainerDto updateTrainer(TrainerUpdateDto dto) {
        Trainer trainer = TrainerMapper.mapUpdateToTrainer(dto);

        return TrainerMapper.mapToDto(trainerRepository.update(trainer));
    }

    @Override
    public TrainerDto getTrainer(String username) {
        Trainer trainer = trainerRepository.get(username);
        return TrainerMapper.mapToDto(trainer);
    }
}