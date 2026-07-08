package com.epam.gymapp.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrainerWorkloadSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainerWorkloadSpringBootApplication.class, args);
    }
}
