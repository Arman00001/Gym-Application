package com.epam.gymapp.persistence.repository;

import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.entity.User;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void save_shouldPersistTrainer() {
        User savedUser = userRepository.saveAndFlush(user(null, "Alex.Brown"));
        TrainingType savedType = trainingTypeRepository.saveAndFlush(trainingType(null, "Yoga"));

        Trainer trainer = trainer(null, savedUser, savedType);

        Trainer saved = trainerRepository.saveAndFlush(trainer);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(saved.getSpecialization().getName()).isEqualTo("Yoga");
    }

    @Test
    void getByUsername_shouldReturnTrainer_whenExists() {
        User savedUser = userRepository.saveAndFlush(user(null, "Alex.Brown"));
        TrainingType savedType = trainingTypeRepository.saveAndFlush(trainingType(null, "Yoga"));
        Trainer savedTrainer = trainerRepository.saveAndFlush(trainer(null, savedUser, savedType));

        Optional<Trainer> result = trainerRepository.getByUsername("Alex.Brown");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedTrainer.getId());
        assertThat(result.get().getUser().getUsername()).isEqualTo("Alex.Brown");
    }

    @Test
    void getByUsername_shouldReturnEmptyOptional_whenDoesNotExist() {
        Optional<Trainer> result = trainerRepository.getByUsername("missing");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnTrainer_whenExists() {
        User savedUser = userRepository.saveAndFlush(user(null, "Alex.Brown"));
        TrainingType savedType = trainingTypeRepository.saveAndFlush(trainingType(null, "Yoga"));
        Trainer savedTrainer = trainerRepository.saveAndFlush(trainer(null, savedUser, savedType));

        Optional<Trainer> result = trainerRepository.findById(savedTrainer.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedTrainer.getId());
    }

    @Test
    void save_shouldUpdateExistingTrainer() {
        User savedUser = userRepository.saveAndFlush(user(null, "Alex.Brown"));
        TrainingType yoga = trainingTypeRepository.saveAndFlush(trainingType(null, "Yoga"));
        TrainingType fitness = trainingTypeRepository.saveAndFlush(trainingType(null, "Fitness"));

        Trainer savedTrainer = trainerRepository.saveAndFlush(trainer(null, savedUser, yoga));

        savedTrainer.setSpecialization(fitness);

        Trainer updated = trainerRepository.saveAndFlush(savedTrainer);

        assertThat(updated.getId()).isEqualTo(savedTrainer.getId());
        assertThat(updated.getSpecialization().getName()).isEqualTo("Fitness");
    }

    @Test
    void findAll_shouldReturnAllTrainers() {
        TrainingType yoga = trainingTypeRepository.saveAndFlush(trainingType(null, "Yoga"));
        TrainingType fitness = trainingTypeRepository.saveAndFlush(trainingType(null, "Fitness"));

        User user1 = userRepository.saveAndFlush(user(null, "Alex.Brown"));
        User user2 = userRepository.saveAndFlush(user(null, "Bob.Green"));

        Trainer trainer1 = trainerRepository.saveAndFlush(trainer(null, user1, yoga));
        Trainer trainer2 = trainerRepository.saveAndFlush(trainer(null, user2, fitness));

        List<Trainer> result = trainerRepository.findAll();

        assertThat(result)
                .extracting(Trainer::getId)
                .contains(trainer1.getId(), trainer2.getId());
    }

    @Test
    void getByUsernames_shouldReturnMatchingTrainers() {
        TrainingType yoga = trainingTypeRepository.saveAndFlush(trainingType(null, "Yoga"));

        User user1 = userRepository.saveAndFlush(user(null, "Alex.Brown"));
        User user2 = userRepository.saveAndFlush(user(null, "Bob.Green"));
        User user3 = userRepository.saveAndFlush(user(null, "Carl.White"));

        Trainer trainer1 = trainerRepository.saveAndFlush(trainer(null, user1, yoga));
        Trainer trainer2 = trainerRepository.saveAndFlush(trainer(null, user2, yoga));
        trainerRepository.saveAndFlush(trainer(null, user3, yoga));

        List<Trainer> result = trainerRepository.getByUsernames(
                List.of("Alex.Brown", "Bob.Green")
        );

        assertThat(result)
                .extracting(Trainer::getId)
                .containsExactlyInAnyOrder(trainer1.getId(), trainer2.getId());
    }

    @Test
    void getNotAssignedToTrainee_shouldReturnTrainers_whenNoAssignmentsExist() {
        TrainingType yoga = trainingTypeRepository.saveAndFlush(trainingType(null, "Yoga"));

        User user1 = userRepository.saveAndFlush(user(null, "Alex.Brown"));
        User user2 = userRepository.saveAndFlush(user(null, "Bob.Green"));

        Trainer trainer1 = trainerRepository.saveAndFlush(trainer(null, user1, yoga));
        Trainer trainer2 = trainerRepository.saveAndFlush(trainer(null, user2, yoga));

        List<Trainer> result = trainerRepository.getNotAssignedToTrainee("John.Smith");

        assertThat(result)
                .extracting(Trainer::getId)
                .contains(trainer1.getId(), trainer2.getId());
    }

    private static Trainer trainer(Long id, User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    private static TrainingType trainingType(Long id, String name) {
        TrainingType type = new TrainingType();
        type.setId(id);
        type.setName(name);
        return type;
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Alex");
        user.setLastName("Brown");
        user.setUsername(username);
        user.setPassword("password12");
        user.setIsActive(true);
        return user;
    }
}