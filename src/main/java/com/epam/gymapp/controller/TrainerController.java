package com.epam.gymapp.controller;

import com.epam.gymapp.dto.trainer.TrainerCreateDto;
import com.epam.gymapp.dto.trainer.TrainerCreateResponse;
import com.epam.gymapp.dto.trainer.TrainerDto;
import com.epam.gymapp.dto.trainer.TrainerUpdateDto;
import com.epam.gymapp.service.trainer.TrainerService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/trainers", produces = {"application/JSON"})
@Tag(name = "Trainers", description = "Operations for creating, updating and retrieving trainers in the application")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "Create Trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created trainer"),
    })
    public ResponseEntity<TrainerCreateResponse> createTrainer(@RequestBody @Valid TrainerCreateDto trainerCreateDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(trainerService.createTrainer(trainerCreateDto));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Retrieve Trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer")
    })
    public ResponseEntity<TrainerDto> get(
            @PathVariable @NotBlank String username
    ) {
        return ResponseEntity
                .ok(trainerService.getTrainerByUsername(username));
    }

    @GetMapping("/not-assigned/{traineeUsername}")
    @Operation(summary = "Retrieve trainers not assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainers")
    })
    public ResponseEntity<List<TrainerDto>> getNotAssignedToTrainee(@PathVariable @NotBlank String traineeUsername) {
        return ResponseEntity
                .ok(trainerService.getNotAssignedToTrainee(traineeUsername));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update Trainer")
    @PreAuthorize("#username == authentication.name OR hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer"),
    })
    public ResponseEntity<TrainerDto> update(
            @PathVariable @NotBlank String username,
            @RequestBody @Valid TrainerUpdateDto dto
    ) {
        return ResponseEntity
                .ok(trainerService.updateTrainer(username, dto));
    }

    @PatchMapping("/{username}/is-active")
    @Operation(summary = "Change Active Status of Trainer")
    @PreAuthorize("#username == authentication.name OR hasRole('ADMIN')")
    @ApiResponse(responseCode = "200", description = "Successfully changed active status of the trainer")
    public ResponseEntity<Void> patch(
            @PathVariable @NotBlank String username
    ) {
        trainerService.changeIsActiveStatus(username);
        return ResponseEntity.ok().build();
    }
}