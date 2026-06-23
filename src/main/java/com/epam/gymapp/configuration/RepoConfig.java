package com.epam.gymapp.configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepoConfig {

//    @Bean
//    public EntityManagerFactory entityManagerFactory(@Value("${entityFactoryType}") String factoryType){
//        return Persistence.createEntityManagerFactory(factoryType);
//    }
//
//    @Bean
//    public EntityManager entityManager(EntityManagerFactory entityManagerFactory){
//        return entityManagerFactory.createEntityManager();
//    }
}
