package com.epam.gymapp.service.trainee;

import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.*;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing trainees.
 *
 * <p>
 * Defines operations for creating trainee profiles, updating trainee information,
 * deleting trainees, retrieving trainees, searching trainee trainings, and changing
 * trainee active status.
 * </p>
 */
public interface TraineeService {

    /**
     * Creates a new trainee profile.
     *
     * <p>
     * A user account is created for the trainee, and the generated raw password
     * is returned in the creation response.
     * </p>
     *
     * @param traineeCreateDto the trainee creation data
     * @return the created trainee response containing the generated username and raw password
     */
    TraineeCreateResponse createTrainee(TraineeCreateDto traineeCreateDto);

    /**
     * Updates an existing trainee profile.
     *
     * <p>
     * Updates the trainee's basic user information, date of birth, and address.
     * </p>
     *
     * @param username the username of the trainee to update
     * @param traineeCreateDto the trainee update data
     * @return the updated trainee
     * @throws ResourceNotFoundException if no trainee exists with the given username
     */
    TraineeDto updateTrainee(String username, TraineeUpdateDto traineeCreateDto);

    /**
     * Deletes a trainee by username.
     *
     * @param dto the delete request containing the username of the trainee to delete
     */
    void deleteTraineeByUsername(DeleteRequestDto dto);

    /**
     * Retrieves a trainee by username.
     *
     * @param username the username of the trainee to retrieve
     * @return the trainee with the given username
     * @throws ResourceNotFoundException if no trainee exists with the given username
     */
    TraineeDto getTraineeByUsername(String username);

    /**
     * Searches trainee trainings using the provided search criteria.
     *
     * @param criteria the trainee training search criteria
     * @param username the username of the given trainee
     * @return a list of trainings matching the criteria
     */
    List<TrainingDto> searchTrainings(TraineeTrainingsSearchCriteria criteria, String username);

    /**
     * Toggles the active status of a trainee.
     *
     * @param username the username of the trainee whose active status should be changed
     * @return the trainee with the updated active status
     * @throws ResourceNotFoundException if no trainee exists with the given username
     */
    TraineeDto changeIsActiveStatus(String username);
}