package com.epam.gymapp.service.trainingtype;

import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateResponse;
import com.epam.gymapp.dto.trainingtype.TrainingTypeDto;
import com.epam.gymapp.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing training types.
 *
 * <p>
 * Defines operations for creating, deleting, and retrieving training types.
 * </p>
 */
public interface TrainingTypeService {

    /**
     * Creates a new training type.
     *
     * @param trainingTypeCreateDto the training type creation data
     * @return the created training type response
     */
    TrainingTypeCreateResponse createTrainingType(TrainingTypeCreateDto trainingTypeCreateDto);

    /**
     * Deletes a training type by its id.
     *
     * @param id the id of the training type to delete
     */
    void deleteTrainingType(Long id);

    /**
     * Retrieves all training types.
     *
     * @return a list of all training types
     */
    List<TrainingTypeDto> getAll();

    /**
     * Retrieves a training type by its id.
     *
     * @param id the id of the training type to retrieve
     * @return the training type with the given id
     * @throws ResourceNotFoundException if no training type exists with the given id
     */
    TrainingTypeDto getTrainingTypeById(Long id);

    /**
     * Retrieves a training type by its name.
     *
     * @param name the name of the training type to retrieve
     * @return the training type with the given name
     * @throws ResourceNotFoundException if no training type exists with the given name
     */
    TrainingTypeDto getTrainingTypeByName(String name);
}