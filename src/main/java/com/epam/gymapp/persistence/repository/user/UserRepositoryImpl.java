//package com.epam.gymapp.persistence.repository.user;
//
//import com.epam.gymapp.exception.ResourceNotFoundException;
//import com.epam.gymapp.persistence.entity.User;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityTransaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public class UserRepositoryImpl implements UserRepository {
//    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
//    private EntityManager entityManager;
//
//    @Autowired
//    public void setEntityManager(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    @Override
//    public User save(User user) {
//        EntityTransaction transaction = entityManager.getTransaction();
//
//        try {
//            transaction.begin();
//
//            entityManager.persist(user);
//            transaction.commit();
//            log.debug("Saved user to database. id={}", user.getId());
//            return user;
//        } catch (Exception e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            throw e;
//        }
//    }
//
//    @Override
//    public User update(User user) {
//        EntityTransaction transaction = entityManager.getTransaction();
//
//        try {
//            transaction.begin();
//
//            if (user.getId() == null) {
//                throw new IllegalArgumentException("User id must not be null");
//            }
//            if (entityManager.find(User.class, user.getId()) == null) {
//                log.warn("Cannot update user. Not found. id={}", user.getId());
//                throw new ResourceNotFoundException("User does not exist");
//            }
//            User updatedUser = entityManager.merge(user);
//            transaction.commit();
//            log.debug("Updated user. id={}", updatedUser.getId());
//            return updatedUser;
//        } catch (Exception e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            throw e;
//        }
//    }
//
//    @Override
//    public void delete(Long id) {
//        EntityTransaction transaction = entityManager.getTransaction();
//
//        try {
//            transaction.begin();
//
//            User user = entityManager.find(User.class, id);
//            if (user == null) {
//                log.warn("Cannot delete user. Not found. id={}", id);
//                throw new ResourceNotFoundException("User does not exist");
//            }
//
//            entityManager.remove(user);
//            transaction.commit();
//            log.debug("Deleted user. id={}", id);
//        } catch (Exception e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            throw e;
//        }
//    }
//
//    @Override
//    public void deleteUser(User user) {
//        EntityTransaction transaction = entityManager.getTransaction();
//
//        try {
//            transaction.begin();
//
//            if (user.getId() == null) {
//                throw new IllegalArgumentException("User id must not be null");
//            }
//            User managedUser = entityManager.find(User.class, user.getId());
//            if (managedUser == null) {
//                log.warn("Cannot delete user. Not found. id={}", user.getId());
//                throw new ResourceNotFoundException("User does not exist");
//            }
//
//            entityManager.remove(user);
//            transaction.commit();
//            log.debug("Deleted user. id={}", user.getId());
//        } catch (Exception e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            throw e;
//        }
//    }
//
//    @Override
//    public Optional<User> getById(Long id) {
//        return Optional.ofNullable(entityManager.find(User.class, id));
//    }
//
//    @Override
//    public Optional<User> getByUsername(String username) {
//        return Optional.ofNullable(
//                entityManager
//                        .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
//                        .setParameter("username", username)
//                        .getSingleResultOrNull());
//    }
//
//    @Override
//    public boolean existsByUsername(String username) {
//        return getByUsername(username).isPresent();
//    }
//
//    @Override
//    public boolean isAuthenticated(String username, String password) {
//        return entityManager
//                .createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class)
//                .setParameter("username", username)
//                .setParameter("password", password)
//                .getSingleResultOrNull() != null;
//    }
//
//    @Override
//    public boolean login(String username, String password) {
//        return entityManager
//                .createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class)
//                .setParameter("username",username)
//                .setParameter("password",password)
//                .getSingleResultOrNull() != null;
//    }
//
//    @Override
//    public void changePassword(String username, String newPassword) {
//        User user = entityManager
//                .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
//                .setParameter("username", username)
//                .getSingleResult();
//
//        user.setPassword(newPassword);
//    }
//}
