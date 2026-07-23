package com.epam.gymapp.workload.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.Validator;

/**
 * Configures validation for JMS listener method arguments.
 *
 * <p>Registers the application's {@link Validator} with the message handler
 * method factory used by Spring JMS listeners, enabling validation annotations
 * such as {@code @Valid} on listener parameters.</p>
 */
@Configuration
@RequiredArgsConstructor
public class JmsValidationConfiguration implements JmsListenerConfigurer {
    private final Validator validator;

    @Override
    public void configureJmsListeners(
            JmsListenerEndpointRegistrar registrar
    ) {
        DefaultMessageHandlerMethodFactory methodFactory =
                new DefaultMessageHandlerMethodFactory();

        methodFactory.setValidator(validator);
        methodFactory.afterPropertiesSet();

        registrar.setMessageHandlerMethodFactory(methodFactory);
    }
}