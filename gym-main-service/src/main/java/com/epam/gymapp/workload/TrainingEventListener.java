package com.epam.gymapp.workload;

import com.epam.gymapp.service.workload.TrainerWorkloadIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Handles training workload events after the originating transaction commits.
 *
 * <p>Forwards committed training changes to the workload integration service.
 * Events are processed only after a successful transaction commit.</p>
 */
@Component
@RequiredArgsConstructor
public class TrainingEventListener {
    private final TrainerWorkloadIntegrationService trainerWorkloadIntegrationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTrainingWorkloadEvent(TrainingWorkloadEvent event) {
        trainerWorkloadIntegrationService.sendTrainerWorkload(event.dto());
    }
}
