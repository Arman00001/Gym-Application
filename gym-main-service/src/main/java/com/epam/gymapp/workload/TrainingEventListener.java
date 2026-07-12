package com.epam.gymapp.workload;

import com.epam.gymapp.logging.TransactionConstants;
import com.epam.gymapp.service.workload.TrainerWorkloadIntegrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TrainingEventListener {
    private final TrainerWorkloadIntegrationService trainerWorkloadIntegrationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTrainingWorkloadEvent(TrainingWorkloadEvent event) {
        MDC.put(TransactionConstants.TRANSACTION_ID, event.transactionId());

        try {
            trainerWorkloadIntegrationService.sendTrainerWorkload(event.dto());
        } finally {
            MDC.remove(TransactionConstants.TRANSACTION_ID);
        }
    }
}
