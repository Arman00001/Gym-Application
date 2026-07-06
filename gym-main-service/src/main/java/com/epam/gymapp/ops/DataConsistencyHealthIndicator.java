package com.epam.gymapp.ops;

import com.epam.gymapp.persistence.repository.trainee.TraineeRepository;
import com.epam.gymapp.persistence.repository.trainer.TrainerRepository;
import com.epam.gymapp.persistence.repository.user.UserRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DataConsistencyHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public DataConsistencyHealthIndicator(
            UserRepository userRepository,
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository
    ) {
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Health health() {
        try {
            long usersCount = userRepository.count();
            long traineesCount = traineeRepository.count();
            long trainersCount = trainerRepository.count();
            long profilesCount = traineesCount + trainersCount;

            if (usersCount == profilesCount) {
                return Health.up()
                        .withDetail("usersCount", usersCount)
                        .withDetail("traineesCount", traineesCount)
                        .withDetail("trainersCount", trainersCount)
                        .withDetail("message", "User/profile consistency is valid")
                        .build();
            }

            return Health.down()
                    .withDetail("usersCount", usersCount)
                    .withDetail("traineesCount", traineesCount)
                    .withDetail("trainersCount", trainersCount)
                    .withDetail("expectedUsersCount", profilesCount)
                    .withDetail("message", "User/profile count mismatch")
                    .build();

        } catch (Exception ex) {
            return Health.down()
                    .withDetail("error", ex.getClass().getSimpleName())
                    .withDetail("message", ex.getMessage())
                    .build();
        }
    }
}