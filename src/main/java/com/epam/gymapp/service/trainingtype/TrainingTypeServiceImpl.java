package com.epam.gymapp.service.trainingtype;

import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateDto;
import com.epam.gymapp.dto.trainingtype.TrainingTypeCreateResponse;
import com.epam.gymapp.dto.trainingtype.TrainingTypeDto;
import com.epam.gymapp.mapper.TrainingTypeMapper;
import com.epam.gymapp.persistence.entity.TrainingType;
import com.epam.gymapp.persistence.repository.trainingtype.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private static final Logger log = LoggerFactory.getLogger(TrainingTypeServiceImpl.class);
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public TrainingTypeCreateResponse createTrainee(TrainingTypeCreateDto trainingTypeCreateDto) {
        log.info("Creating training type {}",
                trainingTypeCreateDto.getName());
        TrainingType trainingType = TrainingTypeMapper.INSTANCE.mapCreateToTrainingType(trainingTypeCreateDto);

        TrainingType result = trainingTypeRepository.save(trainingType);

        log.info("Training type created successfully. name={}", result.getName());

        return TrainingTypeMapper.INSTANCE.mapToCreateResponse(result);
    }

    @Override
    public void deleteTrainingType(Long id) {
        log.info("Deleting training type. id={}", id);
        trainingTypeRepository.delete(id);
        log.info("Training type deleted. id={}", id);
    }

    @Override
    public TrainingTypeDto getTrainingTypeById(Long id) {
        log.info("Getting training type. id={}", id);

        TrainingType trainingType = trainingTypeRepository.get(id).orElseThrow(() -> {
            log.warn("TrainingType profile not found. id={}", id);
            return new IllegalArgumentException("TrainingType does not exist");
        });

        return TrainingTypeMapper.INSTANCE.mapToDto(trainingType);
    }

    @Override
    public TrainingTypeDto getTrainingTypeByName(String name) {
        log.info("Getting training type. name={}", name);
        TrainingType trainingType = trainingTypeRepository.getByName(name).orElseThrow(()->{
            log.warn("Training type not found. name={}", name);
            return new IllegalArgumentException("Training type does not exist");
        });

        return TrainingTypeMapper.INSTANCE.mapToDto(trainingType);
    }
}
