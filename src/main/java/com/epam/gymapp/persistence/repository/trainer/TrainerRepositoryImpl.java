package com.epam.gymapp.persistence.repository.trainer;

import com.epam.gymapp.dto.trainer.TrainerTrainingsSearchCriteria;
import com.epam.gymapp.persistence.entity.Trainer;
import com.epam.gymapp.persistence.entity.Training;
import com.epam.gymapp.persistence.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private static final Logger log = LoggerFactory.getLogger(TrainerRepositoryImpl.class);
    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainer save(Trainer trainer) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.persist(trainer);
            transaction.commit();
            log.debug("Saved trainer to database. id={}", trainer.getId());
            return trainer;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Trainer update(Trainer trainer) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if (trainer.getId() == null) {
                throw new IllegalArgumentException("Trainer id must not be null");
            }
            if (entityManager.find(Trainer.class, trainer.getId()) == null) {
                log.warn("Cannot update trainer. Not found in storage. id={}", trainer.getId());
                throw new IllegalArgumentException("Trainer does not exist");
            }
            Trainer updatedTrainer = entityManager.merge(trainer);
            transaction.commit();
            log.debug("Updated trainer in database. id={}", updatedTrainer.getId());
            return updatedTrainer;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Trainer> get(Long id) {
        log.debug("Getting trainer from storage. id={}", id);
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    public Optional<Trainer> getByUserId(Long userId) {
        return Optional.ofNullable(
                entityManager
                        .createQuery("SELECT t FROM Trainer t WHERE t.user.id = :userId", Trainer.class)
                        .setParameter("userId", userId)
                        .getSingleResultOrNull()
        );
    }

    @Override
    public List<Trainer> getAll() {
        return entityManager
                .createQuery("SELECT t FROM Trainer t", Trainer.class)
                .getResultList();
    }

    @Override
    public Optional<Trainer> getByUsername(String username) {
        return Optional.ofNullable(
                entityManager
                        .createQuery("SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class)
                        .setParameter("username", username)
                        .getSingleResultOrNull());
    }

    @Override
    public List<Trainer> getByUsernames(List<String> trainerUsernames) {
        return entityManager
                .createQuery("SELECT t FROM Trainer t WHERE t.user.username IN :trainerUsernames", Trainer.class)
                .setParameter("trainerUsernames",trainerUsernames)
                .getResultList()
                ;
    }

    @Override
    public List<Training> getTrainingsByCriteria(TrainerTrainingsSearchCriteria criteria) {
        return entityManager
                .createQuery("SELECT trainings " +
                        "FROM Trainer t " +
                        "JOIN t.trainings trainings " +
                        "WHERE t.user.username = :username " +
                        "AND (:firstName IS NULL OR LOWER(trainings.trainee.user.firstName) LIKE LOWER(CONCAT('%',:firstName,'%'))) " +
                        "AND (:lastName IS NULL OR LOWER(trainings.trainee.user.lastName) LIKE LOWER(CONCAT('%',:lastName,'%'))) " +
                        "AND (:fromDate IS NULL OR trainings.date >= :fromDate) " +
                        "AND (:toDate IS NULL OR trainings.date <= :toDate)", Training.class)
                .setParameter("username", criteria.getUsername())
                .setParameter("firstName", criteria.getTraineeFirstName())
                .setParameter("lastName", criteria.getTraineeLastName())
                .setParameter("fromDate", criteria.getFromDate())
                .setParameter("toDate", criteria.getToDate())
                .getResultList();
    }


    @Override
    public List<Trainer> getNotAssignedToTrainee(String username){
        return entityManager
                .createQuery("SELECT t FROM Trainer t " +
                        "WHERE t.id NOT IN (" +
                            "SELECT assignedTrainer.id FROM Trainee t JOIN t.trainers assignedTrainer " +
                            "WHERE t.user.username = :username" +
                        ")", Trainer.class)
                .setParameter("username",username)
                .getResultList();
    }

    @Override
    public Trainer changeIsActiveStatus(String username) {
        Trainer trainer = entityManager
                .createQuery("SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class)
                .setParameter("username", username)
                .getSingleResult();

        User user = trainer.getUser();
        user.setIsActive(!user.getIsActive());

        return trainer;
    }
}
