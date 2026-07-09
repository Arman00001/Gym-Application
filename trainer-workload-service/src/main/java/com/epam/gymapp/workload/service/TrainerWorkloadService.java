package com.epam.gymapp.workload.service;

import com.epam.gymapp.workload.dto.TrainerActionDto;
import com.epam.gymapp.workload.dto.TrainerWorkloadDto;
import jakarta.persistence.EntityNotFoundException;

/**
 * Service interface for managing trainer workload summaries.
 *
 * <p>
 * Defines operations for updating a trainer's monthly workload and retrieving
 * workload information by trainer username, year, and month.
 * </p>
 */
public interface TrainerWorkloadService {

    /**
     * Updates a trainer's workload according to the provided action.
     *
     * <p>
     * An {@code ADD} action increases the monthly training duration summary.
     * A {@code DELETE} action decreases it.
     * </p>
     *
     * @param trainerActionDto the trainer workload action data
     * @throws IllegalArgumentException if a delete action cannot be applied
     *                                  or the duration would become negative
     */
    void updateTrainerWorkload(TrainerActionDto trainerActionDto);

    /**
     * Retrieves a trainer workload summary by username, year, and month.
     *
     * @param username the trainer username
     * @param year the workload year
     * @param month the workload month
     * @return the trainer workload summary
     * @throws EntityNotFoundException if no workload summary exists for the given data
     */
    TrainerWorkloadDto getTrainerWorkload(String username, Integer year, Integer month);
}