package com.epam.gymapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@EnableFeignClients(basePackages = "com.epam.gymapp.workload")
@SpringBootApplication
public class GymSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymSpringBootApplication.class,args);
    }
}
