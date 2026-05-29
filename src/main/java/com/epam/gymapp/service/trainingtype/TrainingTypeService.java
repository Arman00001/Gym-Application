package com.epam.gymapp.service.trainingtype;

import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateResponse;
import com.epam.gymapp.dto.trainingtype.TrainingTypeDto;

public interface TrainingTypeService {
    TrainingTypeCreateResponse createTrainee(TrainingTypeCreateDto trainingTypeCreateDto);
    void deleteTrainingType(Long id);
    TrainingTypeDto getTrainingTypeById(Long id);
    TrainingTypeDto getTrainingTypeByName(String name);
}
