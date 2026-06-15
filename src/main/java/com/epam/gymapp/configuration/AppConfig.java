package com.epam.gymapp.configuration;

import com.epam.gymapp.GymFacade;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {
        "com.epam.gymapp.service",
        "com.epam.gymapp.persistence",
        "com.epam.gymapp.mapper",
        "com.epam.gymapp.util",
        "com.epam.gymapp.exception"
})
@Import(GymFacade.class)
@PropertySource("classpath:application.properties")
public class AppConfig {
}
