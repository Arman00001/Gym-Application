package com.epam.gymapp.service.workload;

import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;
import com.epam.gymapp.workload.WorkloadMessageClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadIntegrationService {

    private final WorkloadMessageClient workloadMessageClient;

    @Value("${app.messaging.trainer-workload-queue}")
    private String destination;

    @CircuitBreaker(
            name = "workloadServiceCircuitBreaker",
            fallbackMethod = "sendTrainerWorkloadFallback"
    )
    public void sendTrainerWorkload(TrainerActionDto request) {
        log.info(
                "Sending trainer workload update. username={}, actionType={}, date={}, duration={}",
                request.getUsername(),
                request.getActionType(),
                request.getTrainingDate(),
                request.getDuration()
        );

        workloadMessageClient.sendWorkloadUpdate(destination, request);

        log.info(
                "Trainer workload update sent successfully. username={}, actionType={}",
                request.getUsername(),
                request.getActionType()
        );
    }

    public void sendTrainerWorkloadFallback(TrainerActionDto request, Throwable ex) {
        log.error(
                "Failed to send trainer workload update. username={}, actionType={}, date={}, duration={}. Reason: {}",
                request.getUsername(),
                request.getActionType(),
                request.getTrainingDate(),
                request.getDuration(),
                ex.getMessage(),
                ex
        );
    }
}