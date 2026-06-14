package com.epam.gymapp.service.trainingtype;

import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateResponse;
import com.epam.gymapp.dto.trainingtype.TrainingTypeDto;

import java.util.List;

public interface TrainingTypeService {
    TrainingTypeCreateResponse createTrainee(TrainingTypeCreateDto trainingTypeCreateDto);
    void deleteTrainingType(Long id);
    List<TrainingTypeDto> getAll();
    TrainingTypeDto getTrainingTypeById(Long id);
    TrainingTypeDto getTrainingTypeByName(String name);
}
