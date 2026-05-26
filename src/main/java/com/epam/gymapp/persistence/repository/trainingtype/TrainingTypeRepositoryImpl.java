package com.epam.gymapp.persistence.repository.trainingtype;

import com.epam.gymapp.persistence.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    private static final Logger log = LoggerFactory.getLogger(TrainingTypeRepositoryImpl.class);
    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TrainingType save(TrainingType trainingType) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.persist(trainingType);
            transaction.commit();
            log.debug("Saved trainingType to database. id={}", trainingType.getId());
            return trainingType;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public TrainingType update(TrainingType trainingType) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if (trainingType.getId() == null) {
                throw new IllegalArgumentException("Training id must not be null");
            }
            if (entityManager.find(TrainingType.class, trainingType.getId()) == null) {
                log.warn("Cannot update trainingType. Not found in storage. id={}", trainingType.getId());
                throw new IllegalArgumentException("TrainingType does not exist");
            }
            TrainingType updatedTrainingType = entityManager.merge(trainingType);
            transaction.commit();
            log.debug("Updated trainingType in database. id={}", updatedTrainingType.getId());
            return updatedTrainingType;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            TrainingType trainingType = entityManager.find(TrainingType.class, id);
            if (trainingType != null) {
                entityManager.remove(trainingType);
            } else {
                log.warn("Cannot delete trainingType. Not found. id={}", id);
                throw new IllegalArgumentException("TrainingType does not exist");
            }
            transaction.commit();

            log.debug("Deleted trainingType. id={}", id);
        } catch (Exception e){
            if(transaction.isActive()){
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void delete(TrainingType trainingType) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if (trainingType.getId() == null) {
                throw new IllegalArgumentException("TrainingType id must not be null");
            }
            TrainingType managedTrainingType = entityManager.find(TrainingType.class, trainingType.getId());
            if (managedTrainingType == null) {
                log.warn("Cannot delete trainingType. Not found. id={}", trainingType.getId());
                throw new IllegalArgumentException("TrainingType does not exist");
            }

            entityManager.remove(managedTrainingType);
            transaction.commit();
            log.debug("Deleted trainingType. id={}", trainingType.getId());
        } catch (Exception e){
            if(transaction.isActive()){
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<TrainingType> get(Long id) {
        log.debug("Getting training from storage. id={}", id);
        return Optional.ofNullable(entityManager.find(TrainingType.class, id));
    }

    @Override
    public Optional<TrainingType> getByName(String name) {
        return Optional.ofNullable(
                entityManager
                        .createQuery("SELECT t FROM TrainingType t WHERE t.name=:name", TrainingType.class)
                        .setParameter("name", name)
                        .getSingleResultOrNull()
        );
    }

    @Override
    public List<TrainingType> getAll() {
        return entityManager
                .createQuery("SELECT t FROM TrainingType t", TrainingType.class)
                .getResultList();
    }
}
