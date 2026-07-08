package com.epam.gymapp.workload.service;

import com.epam.gymapp.workload.dto.TrainerActionDto;
import com.epam.gymapp.workload.dto.TrainerWorkloadDto;
import com.epam.gymapp.workload.mapper.TrainerWorkloadMapper;
import com.epam.gymapp.workload.persistence.entity.ActionType;
import com.epam.gymapp.workload.persistence.entity.TrainerWorkload;
import com.epam.gymapp.workload.persistence.repository.TrainerWorkloadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);


    @Override
    @Transactional
    public void updateTrainerWorkload(TrainerActionDto trainerActionDto) {
        log.info("Update trainer workload \nusername: {}", trainerActionDto.getUsername());

        TrainerWorkload workload = TrainerWorkloadMapper.INSTANCE.mapToFullWorkload(trainerActionDto);
        Optional<TrainerWorkload> existingWorkloadOptional = trainerWorkloadRepository.findByUsernameAndYearAndMonth(
                workload.getUsername(),
                workload.getYear(),
                workload.getMonth()
        );

        if (trainerActionDto.getActionType() == ActionType.DELETE && existingWorkloadOptional.isEmpty()) {
            log.warn("Attempt to perform DELETE on non-existent Trainer record");

            throw new IllegalArgumentException("Action cannot be performed on a non-existent resource");
        }

        TrainerWorkload trainerWorkload = existingWorkloadOptional.orElseGet(() -> {
            TrainerWorkload tw = new TrainerWorkload();
            tw.setYear(workload.getYear());
            tw.setMonth(workload.getMonth());
            tw.setUsername(workload.getUsername());
            tw.setFirstName(workload.getFirstName());
            tw.setLastName(workload.getLastName());
            tw.setTrainingSummaryDuration(0L);
            tw.setStatus(workload.getStatus());
            return tw;
        });

        switch (trainerActionDto.getActionType()) {
            case ADD -> {
                Long newDurationSummary = trainerWorkload.getTrainingSummaryDuration() + workload.getTrainingSummaryDuration();
                trainerWorkload.setTrainingSummaryDuration(newDurationSummary);
            }
            case DELETE -> {
                Long newDurationSummary = trainerWorkload.getTrainingSummaryDuration() - workload.getTrainingSummaryDuration();
                if (newDurationSummary < 0) {
                    throw new IllegalArgumentException("Training duration cannot become negative");
                }
                trainerWorkload.setTrainingSummaryDuration(newDurationSummary);
            }
        }

        if (trainerWorkload.getTrainingSummaryDuration() == 0) {
            log.info("Delete Trainer row without trainings");
            trainerWorkloadRepository.delete(trainerWorkload);
        } else {
            trainerWorkload.setFirstName(workload.getFirstName());
            trainerWorkload.setLastName(workload.getLastName());
            trainerWorkload.setStatus(workload.getStatus());

            log.info("Save updated trainer \nusername: {}", workload.getUsername());
            trainerWorkloadRepository.save(trainerWorkload);
            log.info("Saved updated trainer \nusername: {}", workload.getUsername());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerWorkloadDto getTrainerWorkload(String username, Integer year, Integer month) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsernameAndYearAndMonth(username, year, month)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
        return TrainerWorkloadMapper.INSTANCE.mapToDto(trainerWorkload);
    }

}
