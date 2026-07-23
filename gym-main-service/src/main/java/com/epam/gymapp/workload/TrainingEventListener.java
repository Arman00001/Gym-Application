package com.epam.gymapp.workload;

import com.epam.gymapp.service.workload.TrainerWorkloadIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TrainingEventListener {
    private final TrainerWorkloadIntegrationService trainerWorkloadIntegrationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTrainingWorkloadEvent(TrainingWorkloadEvent event) {
        trainerWorkloadIntegrationService.sendTrainerWorkload(event.dto());
    }
}
