package com.epam.gymapp.persistence.repository.training;

import com.epam.gymapp.persistence.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private static final Logger log = LoggerFactory.getLogger(TrainingRepositoryImpl.class);
    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Training save(Training training) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.persist(training);
            transaction.commit();
            log.debug("Saved training to database. id={}", training.getId());
            return training;
        } catch (Exception e){
            if(transaction.isActive()){
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Training> get(Long id) {
        log.debug("Getting training from storage. id={}", id);
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }
}
