package com.epam.gymapp.controller;

import com.epam.gymapp.dto.AuthenticationRequestDto;
import com.epam.gymapp.dto.DeleteRequestDto;
import com.epam.gymapp.dto.trainee.TraineeCreateDto;
import com.epam.gymapp.dto.trainee.TraineeCreateResponse;
import com.epam.gymapp.dto.trainee.TraineeDto;
import com.epam.gymapp.dto.trainee.TraineeUpdateDto;
import com.epam.gymapp.service.trainee.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/trainees", produces = {"application/JSON"})
@Tag(name = "Trainees", description = "Operations for creating, updating, retrieving and deleting trainees in the application")
@RequiredArgsConstructor
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    @Operation(summary = "Create Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created trainee"),
    })
    public ResponseEntity<TraineeCreateResponse> createTrainee(@RequestBody @Valid TraineeCreateDto traineeCreateDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(traineeService.createTrainee(traineeCreateDto));
    }

    @GetMapping
    @Operation(summary = "Retrieve Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee"),
    })
    public ResponseEntity<TraineeDto> get(@RequestParam("username") String username, @RequestParam("password") String password){
        var auth = new AuthenticationRequestDto();
        auth.setUsername(username);
        auth.setPassword(password);

        return ResponseEntity
                .ok(traineeService.getTraineeByUsername(auth));
    }

    @PutMapping
    @Operation(summary = "Update Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee"),
    })
    public ResponseEntity<TraineeDto> update(@RequestBody @Valid TraineeUpdateDto dto){
        return ResponseEntity
                .ok(traineeService.updateTrainee(dto));
    }

    @PatchMapping("/is-active")
    @Operation(summary = "Change Active Status of Trainee")
    @ApiResponse(responseCode = "200", description = "Successfully changed active status of the trainee")
    public ResponseEntity<Void> patch(@RequestBody @Valid AuthenticationRequestDto dto){
        traineeService.changeIsActiveStatus(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted trainee"),
    })
    public ResponseEntity<String> delete(@RequestBody @Valid DeleteRequestDto dto){
        traineeService.deleteTraineeByUsername(dto);
        return ResponseEntity.ok().body("Deleted successfully");

    }
}
