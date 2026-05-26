package com.epam.gymapp.persistence;

import com.epam.gymapp.persistence.entity.Trainee;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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

@Component
public class StorageInitializer {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    private final EntityManager entityManager;

    @Value("${storage.init.file}")
    private String initFilePath;

    public StorageInitializer(
            @Qualifier("entityManager") EntityManager entityManager
    ) {
        this.entityManager = entityManager;

    }

    @PostConstruct
    public void init() {
        log.info("Initializing storage from file: {}", initFilePath);

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            ClassPathResource resource = new ClassPathResource(initFilePath);

            if (!resource.exists()) {
                log.warn("Storage initialization file was not found: {}", initFilePath);
                transaction.rollback();
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

                entityManager.flush();
                transaction.commit();

                log.info(
                        "Storage initialized successfully. users={}, trainees={}, trainers={}, trainings={}",
                        entityManager.createQuery("SELECT COUNT(u) FROM User u").getSingleResult(),
                        entityManager.createQuery("SELECT COUNT(t) FROM Trainee t").getSingleResult(),
                        entityManager.createQuery("SELECT COUNT(t) FROM Trainer t").getSingleResult(),
                        entityManager.createQuery("SELECT COUNT(t) FROM Training t").getSingleResult()
                );
                log.info(
                        "Users {}",entityManager.createQuery("SELECT u.username FROM User u").getResultList()
                );

            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
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

//        user.setId(Long.parseLong(parts[1]));
        user.setFirstName(parts[2]);
        user.setLastName(parts[3]);
        user.setUsername(parts[4]);
        user.setPassword(parts[5]);
        user.setIsActive(Boolean.parseBoolean(parts[6]));

        entityManager.persist(user);

        log.debug("Initialized user: id={}, username={}", user.getId(), user.getUsername());
    }

    private void parseTrainee(String[] parts) {
        validateLength(parts, 5, "TRAINEE");

        Trainee trainee = new Trainee();

//        trainee.setId(Long.parseLong(parts[1]));
        trainee.setUser(entityManager.find(User.class, Long.parseLong(parts[2])));
        trainee.setDateOfBirth(OffsetDateTime.parse(parts[3]));
        trainee.setAddress(parts[4]);

        validateUserExists(trainee.getUser().getId(), "TRAINEE");

        entityManager.persist(trainee);

        log.debug(
                "Initialized trainee: id={}, userId={}",
                trainee.getId(),
                trainee.getUser().getId()
        );
    }

    private void parseTrainer(String[] parts) {
        validateLength(parts, 5, "TRAINER");

        Trainer trainer = new Trainer();

//        trainer.setId(Long.parseLong(parts[1]));
        trainer.setUser(entityManager.find(User.class, Long.parseLong(parts[2])));

        TrainingType trainingType = getOrCreateTrainingType(parts[4]);
        trainer.setSpecialization(trainingType);

        validateUserExists(trainer.getUser().getId(), "TRAINER");

        entityManager.persist(trainer);

        log.debug(
                "Initialized trainer: id={}, userId={}",
                trainer.getId(),
                trainer.getUser().getId()
        );
    }

    private void parseTraining(String[] parts) {
        validateLength(parts, 8, "TRAINING");

        Training training = new Training();

//        training.setId(Long.parseLong(parts[1]));
        training.setTrainee(entityManager.find(Trainee.class, Long.parseLong(parts[2])));
        training.setTrainer(entityManager.find(Trainer.class, Long.parseLong(parts[3])));
        training.setName(parts[4]);

        TrainingType trainingType = getOrCreateTrainingType(parts[5]);
        training.setType(trainingType);

        training.setDate(OffsetDateTime.parse(parts[6]));
        training.setDuration(Duration.ofMinutes(Long.parseLong(parts[7])));

        validateTraineeExists(training.getTrainee().getId());
        validateTrainerExists(training.getTrainer().getId());

        entityManager.persist(training);

        log.debug("Initialized training: id={}", training.getId());
    }

    private void validateUserExists(Long userId, String recordType) {
        if (entityManager.find(User.class, userId) == null)
            throw new IllegalArgumentException(
                    recordType + " references non-existing userId=" + userId
            );
    }

    private void validateTraineeExists(Long traineeId) {
        if (entityManager.find(Trainee.class, traineeId) == null) {
            throw new IllegalArgumentException(
                    "TRAINING references non-existing traineeId=" + traineeId
            );
        }
    }

    private void validateTrainerExists(Long trainerId) {
        if (entityManager.find(Trainer.class, trainerId) == null) {
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

    private TrainingType getOrCreateTrainingType(String name) {
        return entityManager
                .createQuery(
                        "SELECT t FROM TrainingType t WHERE t.name = :name",
                        TrainingType.class
                )
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    TrainingType trainingType = new TrainingType();
                    trainingType.setName(name);
                    entityManager.persist(trainingType);
                    return trainingType;
                });
    }
}