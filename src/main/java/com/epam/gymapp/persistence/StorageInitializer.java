package com.epam.gymapp.persistence;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class StorageInitializer {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    private final Map<Long, User> users;
    private final Map<Long, Trainee> trainees;
    private final Map<Long, Trainer> trainers;
    private final Map<Long, Training> trainings;

    @Value("${storage.init.file}")
    private String initFilePath;

    public StorageInitializer(
            @Qualifier("userStorage") Map<Long, User> users,
            @Qualifier("traineeStorage") Map<Long, Trainee> trainees,
            @Qualifier("trainerStorage") Map<Long, Trainer> trainers,
            @Qualifier("trainingStorage") Map<Long, Training> trainings
    ) {
        this.users = users;
        this.trainees = trainees;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing storage from file: {}", initFilePath);

        ClassPathResource resource = new ClassPathResource(initFilePath);

        if (!resource.exists()) {
            log.warn("Storage initialization file was not found: {}", initFilePath);
            return;
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

                parseLine(line);
            }

            log.info(
                    "Storage initialized successfully. users={}, trainees={}, trainers={}, trainings={}",
                    users.size(),
                    trainees.size(),
                    trainers.size(),
                    trainings.size()
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
            case "TRAINING" -> parseTraining(parts);
            default -> log.warn("Unknown storage record type: {}", type);
        }
    }

    private void parseUser(String[] parts) {
        validateLength(parts, 7, "USER");

        User user = new User();

        user.setId(Long.parseLong(parts[1]));
        user.setFirstName(parts[2]);
        user.setLastName(parts[3]);
        user.setUsername(parts[4]);
        user.setPassword(parts[5]);
        user.setIsActive(Boolean.parseBoolean(parts[6]));

        users.put(user.getId(), user);

        log.debug("Initialized user: id={}, username={}", user.getId(), user.getUsername());
    }

    private void parseTrainee(String[] parts) {
        validateLength(parts, 5, "TRAINEE");

        Trainee trainee = new Trainee();

        trainee.setId(Long.parseLong(parts[1]));
        trainee.setUserId(Long.parseLong(parts[2]));
        trainee.setDateOfBirth(OffsetDateTime.parse(parts[3]));
        trainee.setAddress(parts[4]);

        validateUserExists(trainee.getUserId(), "TRAINEE");

        trainees.put(trainee.getId(), trainee);

        log.debug(
                "Initialized trainee: id={}, userId={}",
                trainee.getId(),
                trainee.getUserId()
        );
    }

    private void parseTrainer(String[] parts) {
        validateLength(parts, 5, "TRAINER");

        Trainer trainer = new Trainer();

        trainer.setId(Long.parseLong(parts[1]));
        trainer.setUserId(Long.parseLong(parts[2]));
        trainer.setSpecialization(parts[3]);

        TrainingType trainingType = new TrainingType();
        trainingType.setName(parts[4]);
        trainer.setType(trainingType);

        validateUserExists(trainer.getUserId(), "TRAINER");

        trainers.put(trainer.getId(), trainer);

        log.debug(
                "Initialized trainer: id={}, userId={}",
                trainer.getId(),
                trainer.getUserId()
        );
    }

    private void parseTraining(String[] parts) {
        validateLength(parts, 8, "TRAINING");

        Training training = new Training();

        training.setId(Long.parseLong(parts[1]));
        training.setTraineeId(Long.parseLong(parts[2]));
        training.setTrainerId(Long.parseLong(parts[3]));
        training.setName(parts[4]);

        TrainingType trainingType = new TrainingType();
        trainingType.setName(parts[5]);
        training.setType(trainingType);

        training.setDate(OffsetDateTime.parse(parts[6]));
        training.setDuration(Duration.ofMinutes(Long.parseLong(parts[7])));

        validateTraineeExists(training.getTraineeId());
        validateTrainerExists(training.getTrainerId());

        trainings.put(training.getId(), training);

        log.debug("Initialized training: id={}", training.getId());
    }

    private void validateUserExists(Long userId, String recordType) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException(
                    recordType + " references non-existing userId=" + userId
            );
        }
    }

    private void validateTraineeExists(Long traineeId) {
        if (!trainees.containsKey(traineeId)) {
            throw new IllegalArgumentException(
                    "TRAINING references non-existing traineeId=" + traineeId
            );
        }
    }

    private void validateTrainerExists(Long trainerId) {
        if (!trainers.containsKey(trainerId)) {
            throw new IllegalArgumentException(
                    "TRAINING references non-existing trainerId=" + trainerId
            );
        }
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
}