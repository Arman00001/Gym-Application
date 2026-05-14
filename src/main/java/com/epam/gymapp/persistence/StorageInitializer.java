package com.epam.gymapp.persistence;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.entity.TrainingType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Storage storage;

    @Value("${storage.init.file}")
    private String initFilePath;

    public StorageInitializer(Storage storage) {
        this.storage = storage;
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
                    "Storage initialized successfully. trainees={}, trainers={}, trainings={}",
                    storage.getTrainees().size(),
                    storage.getTrainers().size(),
                    storage.getTrainings().size()
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
            case "TRAINEE" -> parseTrainee(parts);
            case "TRAINER" -> parseTrainer(parts);
            case "TRAINING" -> parseTraining(parts);
            default -> log.warn("Unknown storage record type: {}", type);
        }
    }

    private void parseTrainee(String[] parts) {
        validateLength(parts, 10, "TRAINEE");

        Trainee trainee = new Trainee();

        trainee.setId(Long.parseLong(parts[1]));
        trainee.setUserId(Long.parseLong(parts[2]));
        trainee.setFirstName(parts[3]);
        trainee.setLastName(parts[4]);
        trainee.setUsername(parts[5]);
        trainee.setPassword(parts[6]);
        trainee.setIsActive(Boolean.parseBoolean(parts[7]));
        trainee.setDateOfBirth(OffsetDateTime.parse(parts[8]));
        trainee.setAddress(parts[9]);

        Map<String, Trainee> trainees = storage.getTrainees();
        trainees.put(trainee.getUsername(), trainee);

        log.debug("Initialized trainee: username={}", trainee.getUsername());
    }

    private void parseTrainer(String[] parts) {
        validateLength(parts, 10, "TRAINER");

        Trainer trainer = new Trainer();

        trainer.setId(Long.parseLong(parts[1]));
        trainer.setUserId(Long.parseLong(parts[2]));
        trainer.setFirstName(parts[3]);
        trainer.setLastName(parts[4]);
        trainer.setUsername(parts[5]);
        trainer.setPassword(parts[6]);
        trainer.setIsActive(Boolean.parseBoolean(parts[7]));
        trainer.setSpecialization(parts[8]);

        TrainingType trainingType = new TrainingType();
        trainingType.setName(parts[9]);
        trainer.setType(trainingType);

        Map<String, Trainer> trainers = storage.getTrainers();
        trainers.put(trainer.getUsername(), trainer);

        log.debug("Initialized trainer: username={}", trainer.getUsername());
    }

    private void parseTraining(String[] parts) {
        validateLength(parts, 8, "TRAINING");

        Training training = new Training();

        training.setId(Long.parseLong(parts[1]));
        training.setTraineeUsername(parts[2]);
        training.setTrainerUsername(parts[3]);
        training.setName(parts[4]);

        TrainingType trainingType = new TrainingType();
        trainingType.setName(parts[5]);
        training.setType(trainingType);

        training.setDate(OffsetDateTime.parse(parts[6]));
        training.setDuration(Duration.ofMinutes(Long.parseLong(parts[7])));

        Map<Long, Training> trainings = storage.getTrainings();
        trainings.put(training.getId(), training);

        log.debug("Initialized training: id={}", training.getId());
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