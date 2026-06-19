package com.epam.gymapp.controller;

import com.epam.gymapp.dto.trainee.TraineeTrainingsSearchCriteria;
import com.epam.gymapp.dto.trainer.TrainerTrainingsSearchCriteria;
import com.epam.gymapp.dto.training.TrainingCreateDto;
import com.epam.gymapp.dto.training.TrainingDto;
import com.epam.gymapp.service.trainee.TraineeService;
import com.epam.gymapp.service.trainer.TrainerService;
import com.epam.gymapp.service.training.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/trainings", produces = {"application/JSON"})
@Tag(name = "Trainings", description = "Operations for creating, updating, retrieving and deleting trainings in the application")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @GetMapping("/trainees/{username}")
    @Operation(summary = "Get Trainee Trainings List")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(
            @PathVariable @NotBlank String username,
            @ModelAttribute @Valid TraineeTrainingsSearchCriteria criteria
    ) {
        criteria.setUsername(username);

        return ResponseEntity.ok(traineeService.searchTrainings(criteria));
    }

    @GetMapping("/trainers/{username}")
    @Operation(summary = "Get Trainer Trainings List")
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(
            @PathVariable @NotBlank String username,
            @ModelAttribute @Valid TrainerTrainingsSearchCriteria criteria
    ) {
        criteria.setUsername(username);

        return ResponseEntity.ok(trainerService.searchTrainings(criteria));
    }

    @PostMapping(consumes = {"application/json"})
    @Operation(summary = "Add Training")
    public ResponseEntity<Void> addTraining(
            @RequestBody @Valid TrainingCreateDto dto
    ) {
        trainingService.createTraining(dto);
        return ResponseEntity.ok().build();
    }
}