package com.epam.gymapp.workload.mapper;

import com.epam.gymapp.workload.dto.TrainerActionDto;
import com.epam.gymapp.workload.dto.TrainerWorkloadDto;
import com.epam.gymapp.workload.persistence.entity.TrainerWorkload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for converting trainer workload DTOs and entities.
 *
 * <p>
 * Converts incoming trainer action data into trainer workload entities and
 * maps trainer workload entities to response DTOs.
 * </p>
 */
@Mapper
public interface TrainerWorkloadMapper {
    TrainerWorkloadMapper INSTANCE = Mappers.getMapper(TrainerWorkloadMapper.class);

    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(source = "duration", target = "trainingSummaryDuration")
    @Mapping(source = "isActive", target = "status")
    TrainerWorkload mapToWorkload(TrainerActionDto dto);

    default TrainerWorkload mapToFullWorkload(TrainerActionDto dto) {
        TrainerWorkload trainerWorkload = mapToWorkload(dto);
        trainerWorkload.setYear(dto.getTrainingDate().getYear());
        trainerWorkload.setMonth(dto.getTrainingDate().getMonthValue());

        return trainerWorkload;
    }

    TrainerWorkloadDto mapToDto(TrainerWorkload trainerWorkload);
}
