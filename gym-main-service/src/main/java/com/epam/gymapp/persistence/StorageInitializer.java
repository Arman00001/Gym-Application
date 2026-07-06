package com.epam.gymapp.persistence;

import com.epam.gymapp.persistence.entity.*;
import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StorageInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${storage.init.file}")
    private String initFilePath;

    @Value("${storage.training.types:}")
    private String trainingTypesPath;

    private final Map<Long, User> usersByFileId = new HashMap<>();
    private final Map<Long, Trainee> traineesByFileId = new HashMap<>();
    private final Map<Long, Trainer> trainersByFileId = new HashMap<>();
    private final Map<Long, TrainingType> trainingTypesByFileId = new HashMap<>();
    private final Map<String, TrainingType> trainingTypesByName = new HashMap<>();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Initializing storage from file: {}", initFilePath);

        try {
            ClassPathResource resource = new ClassPathResource(initFilePath);

            if (!resource.exists()) {
                log.warn("Storage initialization file was not found: {}", initFilePath);
                return;
            }

            loadTrainingTypes();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            )) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    parseLine(line);
                }
            }

            log.info(
                    "Storage initialized successfully. users={}, trainees={}, trainers={}, trainings={}",
                    userRepository.count(),
                    traineeRepository.count(),
                    trainerRepository.count(),
                    trainingRepository.count()
            );

            log.info(
                    "Users {}",
                    userRepository.findAll()
                            .stream()
                            .map(User::getUsername)
                            .collect(Collectors.toList())
            );

        } catch (Exception e) {
            log.error("Failed to initialize storage from file: {}", initFilePath, e);
            throw new IllegalStateException("Failed to initialize storage", e);
        }
    }

    private void parseLine(String line) {
        String[] parts = line.split(";");

        String type = parts[0];

        switch (type) {
            case "USER" -> parseUser(parts);
            case "TRAINEE" -> parseTrainee(parts);
            case "TRAINER" -> parseTrainer(parts);
            case "TRAINING_TYPE" -> parseTrainingType(parts);
            case "TRAINING" -> parseTraining(parts);
            default -> log.warn("Unknown storage record type: {}", type);
        }
    }

    private void parseUser(String[] parts) {
        validateLength(parts, 8, "USER");

        Long fileId = Long.parseLong(parts[1]);

        User user = new User();
        user.setFirstName(parts[2]);
        user.setLastName(parts[3]);
        user.setUsername(parts[4]);
        user.setPassword(passwordEncoder.encode(parts[5]));
        user.setIsActive(Boolean.parseBoolean(parts[6]));
        user.setRole(Role.parseRole(parts[7]));

        user = userRepository.save(user);
        usersByFileId.put(fileId, user);

        log.debug(
                "Initialized user: fileId={}, id={}, username={}",
                fileId,
                user.getId(),
                user.getUsername()
        );
    }

    private void parseTrainee(String[] parts) {
        validateLength(parts, 5, "TRAINEE");

        Long fileId = Long.parseLong(parts[1]);
        Long userFileId = Long.parseLong(parts[2]);

        Trainee trainee = new Trainee();
        trainee.setUser(getUser(userFileId, "TRAINEE"));
        trainee.setDateOfBirth(parseDate(parts[3]));
        trainee.setAddress(parts[4]);

        trainee = traineeRepository.save(trainee);
        traineesByFileId.put(fileId, trainee);

        log.debug(
                "Initialized trainee: id={}, userId={}",
                trainee.getId(),
                trainee.getUser().getId()
        );
    }

    private void parseTrainer(String[] parts) {
        validateLength(parts, 5, "TRAINER");

        Long fileId = Long.parseLong(parts[1]);
        Long userFileId = Long.parseLong(parts[2]);

        Trainer trainer = new Trainer();
        trainer.setUser(getUser(userFileId, "TRAINER"));

        TrainingType trainingType = getTrainingType(parts[4]);
        trainer.setSpecialization(trainingType);

        trainer = trainerRepository.save(trainer);
        trainersByFileId.put(fileId, trainer);

        log.debug(
                "Initialized trainer: id={}, userId={}",
                trainer.getId(),
                trainer.getUser().getId()
        );
    }

    private void parseTraining(String[] parts) {
        validateLength(parts, 8, "TRAINING");

        Training training = new Training();

        training.setTrainee(getTrainee(Long.parseLong(parts[2])));
        training.setTrainer(getTrainer(Long.parseLong(parts[3])));
        training.setName(parts[4]);

        TrainingType trainingType = getTrainingType(parts[5]);
        training.setType(trainingType);

        training.setDate(parseDate(parts[6]));
        training.setDuration(Long.parseLong(parts[7]));

        training = trainingRepository.save(training);

        log.debug("Initialized training: id={}", training.getId());
    }

    private User getUser(Long fileId, String recordType) {
        User user = usersByFileId.get(fileId);

        if (user == null) {
            throw new IllegalArgumentException(recordType + " references non-existing userId=" + fileId);
        }

        return user;
    }

    private Trainee getTrainee(Long fileId) {
        Trainee trainee = traineesByFileId.get(fileId);

        if (trainee == null) {
            throw new IllegalArgumentException("TRAINING references non-existing traineeId=" + fileId);
        }

        return trainee;
    }

    private Trainer getTrainer(Long fileId) {
        Trainer trainer = trainersByFileId.get(fileId);

        if (trainer == null) {
            throw new IllegalArgumentException("TRAINING references non-existing trainerId=" + fileId);
        }

        return trainer;
    }

    private LocalDate parseDate(String value) {
        String trimmedValue = value.trim();

        if (trimmedValue.contains("T")) {
            return LocalDate.parse(trimmedValue.substring(0, trimmedValue.indexOf('T')));
        }

        return LocalDate.parse(trimmedValue);
    }

    private void validateLength(String[] parts, int expectedLength, String recordType) {
        if (parts.length != expectedLength) {
            throw new IllegalArgumentException(
                    "Invalid " + recordType + " record. Expected "
                            + expectedLength + " fields but got "
                            + parts.length
            );
        }
    }

    private void loadTrainingTypes() throws Exception {
        if (trainingTypesPath == null || trainingTypesPath.isBlank()) {
            log.warn("No training types file configured. Set storage.training.types in application.properties");
            return;
        }

        log.info("Initializing training types from file: {}", trainingTypesPath);

        ClassPathResource resource = new ClassPathResource(trainingTypesPath);

        if (!resource.exists()) {
            throw new IllegalArgumentException(
                    "Training types initialization file was not found: " + trainingTypesPath
            );
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                parseTrainingType(line.split(";"));
            }
        }

        log.info("Training types initialized successfully. count={}", trainingTypesByName.size());
    }

    private void parseTrainingType(String[] parts) {
        Long fileId = null;
        String name;

        if (parts.length == 3 && "TRAINING_TYPE".equals(parts[0])) {
            fileId = Long.parseLong(parts[1]);
            name = parts[2];
        } else if (parts.length == 2) {
            fileId = Long.parseLong(parts[0]);
            name = parts[1];
        } else if (parts.length == 1) {
            name = parts[0];
        } else {
            throw new IllegalArgumentException("Invalid TRAINING_TYPE record");
        }

        TrainingType trainingType = trainingTypesByName.get(name);

        if (trainingType == null) {
            trainingType = trainingTypeRepository.findByName(name)
                    .orElseGet(() -> {
                        TrainingType newTrainingType = new TrainingType();
                        newTrainingType.setName(name);
                        return trainingTypeRepository.save(newTrainingType);
                    });
        }

        trainingTypesByName.put(name, trainingType);

        if (fileId != null) {
            trainingTypesByFileId.put(fileId, trainingType);
        }

        log.debug("Initialized training type: fileId={}, name={}", fileId, name);
    }

    private TrainingType getTrainingType(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Training type must not be blank");
        }

        String trimmedValue = value.trim();

        if (trimmedValue.matches("\\d+")) {
            TrainingType trainingType = trainingTypesByFileId.get(Long.parseLong(trimmedValue));

            if (trainingType != null) {
                return trainingType;
            }
        }

        TrainingType trainingType = trainingTypesByName.get(trimmedValue);

        if (trainingType != null) {
            return trainingType;
        }

        trainingType = trainingTypeRepository.findByName(trimmedValue).orElse(null);

        if (trainingType != null) {
            trainingTypesByName.put(trimmedValue, trainingType);
            return trainingType;
        }

        throw new IllegalArgumentException(
                "Unknown training type: " + value + ". Add it to " + trainingTypesPath
        );
    }
}