package com.epam.gymapp.workload.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.Validator;

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