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

/**
 * JMS message listener responsible for processing trainer workload updates.
 *
 * <p>The listener consumes {@link TrainerActionDto} messages from the configured
 * trainer workload queue and delegates their processing to
 * {@link TrainerWorkloadService}.</p>
 *
 * <p>The transaction identifier received in the message header is temporarily
 * added to the SLF4J {@link MDC}. This allows all log entries generated while
 * processing the message to be associated with the transaction that originally
 * produced it.</p>
 *
 * <p>After message processing completes, the previous MDC transaction identifier
 * is restored to prevent transaction context from leaking between messages
 * processed by the same listener thread.</p>
 *
 * @see TrainerActionDto
 * @see TrainerWorkloadService
 * @see MDC
 */
@Component
@RequiredArgsConstructor
public class TrainerWorkloadJmsListener {

    private static final Logger log =
            LoggerFactory.getLogger(TrainerWorkloadJmsListener.class);

    /**
     * Service responsible for applying trainer workload changes.
     */
    private final TrainerWorkloadService trainerWorkloadService;

    /**
     * Receives and processes a trainer workload update message.
     *
     * <p> The transaction identifier supplied in the JMS message header is added
     * to the MDC for correlated logging during message processing.</p>
     *
     * <p>The workload update is delegated to
     * {@link TrainerWorkloadService#updateTrainerWorkload(TrainerActionDto)}.
     * </p>
     *
     * @param trainerActionDto validated message payload containing the trainer,
     *                         action type, and workload information
     * @param transactionId    identifier used to correlate logs across services
     *                         participating in the same transaction
     * @throws jakarta.validation.ConstraintViolationException if the message
     *                                                         payload violates
     *                                                         validation constraints
     */
    @JmsListener(destination = "${app.messaging.trainer-workload-queue}")
    public void updateTrainerWorkload(
            @Payload @Valid TrainerActionDto trainerActionDto,
            @Header(name = "transactionId", required = false) String transactionId
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