package com.epam.gymapp.ops;

import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.LongSupplier;

/**
 * Configuration for registering Gym entity count metrics.
 *
 * <p>
 * Registers Micrometer gauges that expose the total number of users, trainees,
 * trainers, trainings, and training types.
 * </p>
 */
@Configuration
public class GymEntityMetricsConfiguration {

    @Bean
    public MeterBinder gymEntityMetrics(
            UserRepository userRepository,
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            TrainingRepository trainingRepository,
            TrainingTypeRepository trainingTypeRepository
    ) {
        return registry -> {
            Gauge.builder("gym_users", userRepository, repository -> safeCount(repository::count))
                    .description("Total number of users")
                    .register(registry);

            Gauge.builder("gym_trainees", traineeRepository, repository -> safeCount(repository::count))
                    .description("Total number of trainees")
                    .register(registry);

            Gauge.builder("gym_trainers", trainerRepository, repository -> safeCount(repository::count))
                    .description("Total number of trainers")
                    .register(registry);

            Gauge.builder("gym_trainings", trainingRepository, repository -> safeCount(repository::count))
                    .description("Total number of trainings")
                    .register(registry);

            Gauge.builder("gym_training_types", trainingTypeRepository, repository -> safeCount(repository::count))
                    .description("Total number of training types")
                    .register(registry);
        };
    }

    /**
     * Safely retrieves an entity count for a gauge.
     *
     * @param supplier the count supplier
     * @return the entity count, or {@code -1} if the count cannot be retrieved
     */
    private static double safeCount(LongSupplier supplier) {
        try {
            return supplier.getAsLong();
        } catch (Exception ex) {
            return -1;
        }
    }
}