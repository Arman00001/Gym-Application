package com.epam.gymapp.workload;

import com.epam.gymapp.dto.trainer.workload.TrainerActionDto;
import com.epam.gymapp.logging.TransactionConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkloadMessageClient {

    private static final String JMS_TRANSACTION_ID = "transactionId";

    private final JmsTemplate jmsTemplate;

    public void sendWorkloadUpdate(
            String destination,
            TrainerActionDto dto
    ) {
        String transactionId =
                MDC.get(TransactionConstants.TRANSACTION_ID);

        jmsTemplate.convertAndSend(destination, dto, message -> {
            if (transactionId != null) {
                message.setStringProperty(JMS_TRANSACTION_ID, transactionId);
            }
            return message;
        });
    }
}
