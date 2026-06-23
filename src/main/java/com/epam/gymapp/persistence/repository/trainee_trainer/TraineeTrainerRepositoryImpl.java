//package com.epam.gymapp.persistence.repository.trainee_trainer;
//
//import com.epam.gymapp.persistence.entity.Trainee;
//import com.epam.gymapp.persistence.entity.TraineeTrainer;
//import com.epam.gymapp.persistence.entity.Trainer;
//import jakarta.persistence.EntityManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public class TraineeTrainerRepositoryImpl implements TraineeTrainerRepository {
//    private EntityManager entityManager;
//
//    @Autowired
//    public void setEntityManager(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    @Override
//    public void updateTrainerList(Trainee trainee, List<Trainer> trainers) {
//        entityManager
//                .createQuery("DELETE FROM TraineeTrainer tt WHERE tt.trainee.id = :traineeId")
//                .setParameter("traineeId", trainee.getId())
//                .executeUpdate();
//
//        for (Trainer trainer : trainers) {
//            TraineeTrainer traineeTrainer = new TraineeTrainer();
//            traineeTrainer.setTrainee(trainee);
//            traineeTrainer.setTrainer(trainer);
//
//            entityManager.persist(traineeTrainer);
//        }
//    }
//
//}
