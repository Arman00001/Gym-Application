package com.epam.gymapp.util;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UsernameGenerator {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    Set<String> existingUsernames;

    @PostConstruct
    void init() {
        existingUsernames = Stream.concat(
                traineeRepository.getAll().stream().map(Trainee::getUsername),
                trainerRepository.getAll().stream().map(Trainer::getUsername)
        ).collect(Collectors.toSet());
    }

    public String generate(String name, String lastName) {
        String baseUsername = name + "." + lastName;

        if (!existingUsernames.contains(baseUsername)) {
            existingUsernames.add(baseUsername);
            return baseUsername;
        }

        int suffix = 1;
        while (existingUsernames.contains(baseUsername + suffix)) {
            suffix++;
        }
        String username = baseUsername + suffix;

        existingUsernames.add(username);

        return username;
    }
}
