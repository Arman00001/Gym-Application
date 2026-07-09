package com.epam.gymapp.service.trainer;

import com.epam.gymapp.dto.trainer.*;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing trainers.
 *
 * <p>
 * Defines operations for creating trainer profiles, updating trainer information,
 * retrieving trainers, searching trainer trainings, and changing trainer active status.
 * </p>
 */
public interface TrainerService {

    /**
     * Creates a new trainer profile.
     *
     * <p>
     * A user account is created for the trainer, and the trainer is assigned
     * the specified specialization.
     * </p>
     *
     * @param trainerCreateDto the trainer creation data
     * @return the created trainer response containing the generated username and raw password
     * @throws ResourceNotFoundException if the specified specialization does not exist
     */
    TrainerCreateResponse createTrainer(TrainerCreateDto trainerCreateDto);

    /**
     * Updates an existing trainer profile.
     *
     * <p>
     * Updates the trainer's basic user information and specialization.
     * </p>
     *
     * @param username   the username of the trainer to update
     * @param trainerDto the trainer update data
     * @return the updated trainer
     * @throws ResourceNotFoundException if the trainer or specialization does not exist
     */
    TrainerDto updateTrainer(String username, TrainerUpdateDto trainerDto);

    /**
     * Retrieves a trainer by username.
     *
     * @param username the username of the trainer to retrieve
     * @return the trainer with the given username
     * @throws ResourceNotFoundException if no trainer exists with the given username
     */
    TrainerDto getTrainerByUsername(String username);

    /**
     * Retrieves trainers that are not assigned to the specified trainee.
     *
     * @param username the username of the trainee
     * @return a list of trainers not assigned to the trainee
     */
    List<TrainerDto> getNotAssignedToTrainee(String username);

    /**
     * Searches trainer trainings using the provided search criteria.
     *
     * @param criteria the trainer training search criteria
     * @param username the username of the given trainer
     * @return a list of trainings matching the criteria
     */
    List<TrainingDto> searchTrainings(TrainerTrainingsSearchCriteria criteria, String username);

    /**
     * Toggles the active status of a trainer.
     *
     * @param username the username of the trainer whose active status should be changed
     * @return the trainer with the updated active status
     * @throws ResourceNotFoundException if no trainer exists with the given username
     */
    TrainerDto changeIsActiveStatus(String username);
}