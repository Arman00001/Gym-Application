package com.epam.gymapp.workload.messaging;

import com.epam.gymapp.workload.dto.TrainerActionDto;
import com.epam.gymapp.workload.logging.TransactionConstants;
import com.epam.gymapp.workload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadJmsListener {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadJmsListener.class);
    private final TrainerWorkloadService trainerWorkloadService;

    @JmsListener(destination = "${app.messaging.trainer-workload-queue}")
    public void updateTrainerWorkload(
            @Payload @Valid TrainerActionDto trainerActionDto,
            @Header(name = "transactionId") String transactionId
    ) {
        String key = TransactionConstants.TRANSACTION_ID;
        String previousTransactionId = MDC.get(key);
        try {
            if (transactionId != null) {
                MDC.put(key, transactionId);
            }
            log.info(
                    "Received trainer workload message: trainer={}, action={}",
                    trainerActionDto.getUsername(),
                    trainerActionDto.getActionType().name()
            );

            trainerWorkloadService.updateTrainerWorkload(trainerActionDto);
            log.info(
                    "Trainer workload message processed: trainer={}",
                    trainerActionDto.getUsername()
            );
        } finally {
            if (previousTransactionId == null) {
                MDC.remove(key);
            } else {
                MDC.put(key, previousTransactionId);
            }
        }
    }
}
