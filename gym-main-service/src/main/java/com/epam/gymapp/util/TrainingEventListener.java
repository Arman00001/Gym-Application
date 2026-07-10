package com.epam.gymapp.util;

import com.epam.gymapp.service.TrainingCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TrainingEventListener {
    private final TrainerWorkloadClient workloadClient;

    public TrainingEventListener(TrainerWorkloadClient workloadClient) {
        this.workloadClient = workloadClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTrainingCreated(TrainingCreatedEvent event) {
        workloadClient.sendTrainerWorkload(event.dto());
    }
}
