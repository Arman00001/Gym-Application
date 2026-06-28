package com.epam.gymapp.ops;

import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.repository.training.TrainingRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Endpoint(id = "gym-training-stats")
public class GymTrainingStatsEndpoint {

    private final TrainingRepository trainingRepository;

    public GymTrainingStatsEndpoint(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @ReadOperation
    public Map<String, Object> trainingStats() {
        var trainings = trainingRepository.findAll();

        long totalTrainings = trainings.size();

        long totalDuration = trainings.stream()
                .map(Training::getDuration)
                .filter(duration -> duration != null)
                .mapToLong(Long::longValue)
                .sum();

        double averageDuration = trainings.stream()
                .map(Training::getDuration)
                .filter(duration -> duration != null)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        long invalidDurationCount = trainings.stream()
                .filter(training -> training.getDuration() == null || training.getDuration() <= 0)
                .count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalTrainings", totalTrainings);
        result.put("totalDuration", totalDuration);
        result.put("averageDuration", averageDuration);
        result.put("invalidDurationCount", invalidDurationCount);

        return result;
    }
}