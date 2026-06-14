package com.epam.gymapp.controller;

import com.epam.gymapp.dto.AuthenticationRequestDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    @Operation(summary = "Retrieve Trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer")
    })
    public ResponseEntity<TrainerDto> get(@RequestParam("username") String username, @RequestParam("password") String password) {
        var auth = new AuthenticationRequestDto();
        auth.setUsername(username);
        auth.setPassword(password);

        return ResponseEntity
                .ok(trainerService.getTrainerByUsername(auth));
    }

    @GetMapping("/is-active/not-assigned")
    @Operation(summary = "Retrieve Trainers list by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainers")
    })
    public ResponseEntity<List<TrainerDto>> getNotAssignedToTrainee(@RequestParam("username") String username) {
        return ResponseEntity
                .ok(trainerService.getNotAssignedToTrainee(username));
    }

    @PutMapping
    @Operation(summary = "Update Trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer"),
    })
    public ResponseEntity<TrainerDto> update(@RequestBody @Valid TrainerUpdateDto dto) {
        return ResponseEntity
                .ok(trainerService.updateTrainer(dto));
    }

    @PatchMapping("/is-active")
    @Operation(summary = "Change Active Status of Trainer")
    @ApiResponse(responseCode = "200", description = "Successfully changed active status of the trainer")
    public ResponseEntity<Void> patch(@RequestBody @Valid AuthenticationRequestDto dto){
        trainerService.changeIsActiveStatus(dto);
        return ResponseEntity.ok().build();
    }
}
