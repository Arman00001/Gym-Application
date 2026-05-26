package com.epam.gymapp.service.trainingtype;

import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateResponse;
import com.epam.gymapp.dto.trainingtype.TrainingTypeDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeUpdateDto;

public interface TrainingTypeService {
    TrainingTypeCreateResponse createTrainee(TrainingTypeCreateDto trainingTypeCreateDto);
    TrainingTypeDto updateTrainee(TrainingTypeUpdateDto trainingTypeUpdateDto);
    void deleteTrainingType(Long id);
    TrainingTypeDto getTrainingTypeById(Long id);
    TrainingTypeDto getTrainingTypeByName(String name);
}
