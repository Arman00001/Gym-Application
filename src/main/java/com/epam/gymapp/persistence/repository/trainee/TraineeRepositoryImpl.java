package com.epam.gymapp.persistence.repository.trainee;

import com.epam.gymapp.dto.trainee.TraineeTrainingsSearchCriteria;
import com.epam.gymapp.persistence.entity.Trainee;
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
public class TraineeRepositoryImpl implements TraineeRepository {
    private static final Logger log = LoggerFactory.getLogger(TraineeRepositoryImpl.class);
    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainee save(Trainee trainee) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(trainee);

            transaction.commit();

            log.debug("Saved trainee to database. id={}", trainee.getId());
            return trainee;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Trainee update(Trainee trainee) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if (trainee.getId() == null) {
                throw new IllegalArgumentException("Trainee id must not be null");
            }
            if (entityManager.find(Trainee.class, trainee.getId()) == null) {
                log.warn("Cannot update trainee. Not found in storage. id={}", trainee.getId());
                throw new IllegalArgumentException("Trainee does not exist");
            }
            Trainee updatedTrainee = entityManager.merge(trainee);

            transaction.commit();

            log.debug("Updated trainee in database. id={}", updatedTrainee.getId());
            return updatedTrainee;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Trainee delete(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee != null) {
                entityManager.remove(trainee);
            } else {
                log.warn("Cannot delete trainee. Not found in storage. id={}", id);
                throw new IllegalArgumentException("Trainee does not exist");
            }

            transaction.commit();
            log.debug("Deleted trainee from storage. id={}", id);
            return trainee;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            int result = entityManager
                    .createQuery("DELETE FROM Trainee t WHERE t.user.id = :userId", Trainee.class)
                    .setParameter("userId", userId)
                    .executeUpdate();
            if (result == 0) {
                log.warn("Trainee not found. userId={}", userId);
                throw new IllegalArgumentException("Trainee does not exist");
            }
            transaction.commit();
            log.info("Trainee with userId = {} removed", userId);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void deleteByUsername(String username) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            int result = entityManager
                    .createQuery("DELETE FROM Trainee t WHERE t.user.username = :username")
                    .setParameter("username", username)
                    .executeUpdate();
            if (result == 0) {
                log.warn("Trainee not found. username={}", username);
                throw new IllegalArgumentException("Trainee does not exist");
            }
            transaction.commit();
            log.info("Trainee deleted. username={}", username);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public List<Trainee> getAll() {
        return entityManager
                .createQuery("SELECT t FROM Trainee t", Trainee.class)
                .getResultList();
    }

    @Override
    public Optional<Trainee> getByUserId(Long userId) {
        return Optional.ofNullable(
                entityManager
                        .createQuery("SELECT t FROM Trainee t WHERE t.user.id = :userId", Trainee.class)
                        .setParameter("userId", userId)
                        .getSingleResultOrNull()
        );
    }

    @Override
    public Optional<Trainee> get(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Override
    public Optional<Trainee> getByUsername(String username) {
        return Optional.ofNullable(
                entityManager
                        .createQuery("SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                        .setParameter("username", username)
                        .getSingleResultOrNull());

    }

    @Override
    public List<Training> getTrainingsByCriteria(TraineeTrainingsSearchCriteria criteria) {
        return entityManager
                .createQuery("SELECT trainings " +
                        "FROM Training trainings " +
                        "WHERE trainings.trainee.user.username = :username " +
                        "AND (:firstName IS NULL OR LOWER(trainings.trainer.user.firstName) LIKE LOWER(CONCAT('%',:firstName,'%'))) " +
                        "AND (:lastName IS NULL OR LOWER(trainings.trainer.user.lastName) LIKE LOWER(CONCAT('%',:lastName,'%'))) " +
                        "AND (:trainingType IS NULL OR LOWER(trainings.type.name) LIKE LOWER(CONCAT('%',:trainingType,'%'))) " +
                        "AND (:fromDate IS NULL OR trainings.date >= :fromDate) " +
                        "AND (:toDate IS NULL OR trainings.date <= :toDate)", Training.class)
                .setParameter("username", criteria.getUsername())
                .setParameter("firstName", criteria.getTrainerFirstName())
                .setParameter("lastName", criteria.getTrainerLastName())
                .setParameter("trainingType", criteria.getTrainingType())
                .setParameter("fromDate", criteria.getFromDate())
                .setParameter("toDate", criteria.getToDate())
                .getResultList();
    }

    @Override
    public List<Trainee> getAllByTrainerUsername(String trainerUsername) {
        return entityManager
                .createQuery("SELECT DISTINCT tt.trainee " +
                        "FROM TraineeTrainer tt " +
                        "WHERE tt.trainer.user.username = :trainerUsername", Trainee.class)
                .setParameter("trainerUsername", trainerUsername)
                .getResultList();
    }

    @Override
    public Trainee changeIsActiveStatus(String username) {
        Trainee trainee = entityManager
                .createQuery("SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .getSingleResult();

        User user = trainee.getUser();
        user.setIsActive(!user.getIsActive());

        return trainee;
    }
}
