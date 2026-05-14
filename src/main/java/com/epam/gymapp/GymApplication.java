package com.epam.gymapp;

import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.OffsetDateTime;

@Configuration
@ComponentScan(basePackages = "com.epam.gymapp")
@PropertySource("classpath:application.properties")
public class GymApplication {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(GymApplication.class)) {

            GymFacade facade = context.getBean(GymFacade.class);

            TraineeDto existing = facade.getTrainee("John.Smith");
            System.out.println("Loaded trainee from init file: " + existing.getFirstName());

            TraineeCreateDto dto = new TraineeCreateDto();
            dto.setFirstName("Test");
            dto.setLastName("User");
            dto.setDateOfBirth(OffsetDateTime.parse("2000-01-01T00:00:00Z"));
            dto.setAddress("Test address");

            TraineeCreateResponse response = facade.createTrainee(dto);

            System.out.println("Created trainee:");
            System.out.println("username = " + response.getUsername());
            System.out.println("password = " + response.getPassword());

            TraineeDto created = facade.getTrainee(response.getUsername());
            System.out.println("Fetched created trainee: " + created.getFirstName());
        }
    }
}