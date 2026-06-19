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
import jakarta.validation.constraints.NotBlank;
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
    public ResponseEntity<TraineeCreateResponse> createTrainee(@RequestBody @Valid TraineeCreateDto traineeCreateDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(traineeService.createTrainee(traineeCreateDto));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Retrieve Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee"),
    })
    public ResponseEntity<TraineeDto> get(
            @PathVariable @NotBlank String username,
            @RequestParam("password") @NotBlank String password
    ) {
        var auth = new AuthenticationRequestDto();
        auth.setUsername(username);
        auth.setPassword(password);

        return ResponseEntity
                .ok(traineeService.getTraineeByUsername(auth));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee"),
    })
    public ResponseEntity<TraineeDto> update(
            @PathVariable @NotBlank String username,
            @RequestBody @Valid TraineeUpdateDto dto
    ) {
        dto.setUsername(username);

        return ResponseEntity
                .ok(traineeService.updateTrainee(dto));
    }

    @PatchMapping("/{username}/is-active")
    @Operation(summary = "Change Active Status of Trainee")
    @ApiResponse(responseCode = "200", description = "Successfully changed active status of the trainee")
    public ResponseEntity<Void> patch(
            @PathVariable @NotBlank String username,
            @RequestParam("password") @NotBlank String password
    ) {
        var auth = new AuthenticationRequestDto();
        auth.setUsername(username);
        auth.setPassword(password);

        traineeService.changeIsActiveStatus(auth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted trainee"),
    })
    public ResponseEntity<String> delete(
            @PathVariable @NotBlank String username,
            @RequestParam("password") @NotBlank String password
    ) {
        var dto = new DeleteRequestDto();
        dto.setUsername(username);
        dto.setPassword(password);

        traineeService.deleteTraineeByUsername(dto);
        return ResponseEntity.ok().body("Deleted successfully");
    }
}