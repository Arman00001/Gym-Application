package com.epam.gymapp.service.training;

import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.exception.ResourceNotFoundException;

/**
 * Service interface for managing trainings.
 *
 * <p>
 * Defines operations for creating, retrieving, and deleting training records.
 * </p>
 */
public interface TrainingService {

    /**
     * Creates a new training.
     *
     * <p>
     * The training is created for the trainee and trainer specified in the request.
     * </p>
     *
     * @param trainingCreateDto the training creation data
     * @return the created training
     * @throws ResourceNotFoundException if the specified trainee or trainer does not exist
     */
    TrainingDto createTraining(TrainingCreateDto trainingCreateDto);

    /**
     * Retrieves a training by its id.
     *
     * @param id the id of the training to retrieve
     * @return the training with the given id
     * @throws ResourceNotFoundException if no training exists with the given id
     */
    TrainingDto getTraining(Long id);

    /**
     * Deletes a training by its id.
     *
     * @param id the id of the training to delete
     */
    void delete(Long id);
}