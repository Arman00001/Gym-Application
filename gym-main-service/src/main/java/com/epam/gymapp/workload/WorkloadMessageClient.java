package com.epam.gymapp.workload;

import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;
import com.epam.gymapp.logging.TransactionConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends trainer workload updates to a JMS destination.
 *
 * <p>The current transaction ID is copied from the SLF4J MDC to a JMS message
 * property when available, allowing logs to be correlated across services.</p>
 */
@Component
@RequiredArgsConstructor
public class WorkloadMessageClient {

    private final JmsTemplate jmsTemplate;

    /**
     * Sends a trainer workload update to the specified JMS destination.
     *
     * @param destination target queue or topic name
     * @param dto workload update to send
     */
    public void sendWorkloadUpdate(
            String destination,
            TrainerActionDto dto
    ) {
        String transactionId =
                MDC.get(TransactionConstants.TRANSACTION_ID);

        jmsTemplate.convertAndSend(destination, dto, message -> {
            if (transactionId != null) {
                message.setStringProperty(TransactionConstants.TRANSACTION_ID, transactionId);
            }
            return message;
        });
    }
}
