package com.epam.gymapp.workload.controller;

import com.epam.gymapp.workload.dto.TrainerActionDto;
import com.epam.gymapp.workload.dto.TrainerWorkloadDto;
import com.epam.gymapp.workload.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/trainer-workloads", produces = {"application/JSON"})
@Tag(name = "Trainer Workload", description = "Operations for adding or deleting trainings from trainer's list in the application")
@RequiredArgsConstructor
public class TrainerWorkloadController {
    private final TrainerWorkloadService trainerWorkloadService;

    @PostMapping
    @Operation(summary = "Update trainer's monthly workload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the workload"),
    })
    public ResponseEntity<Void> updateTrainerWorkload(@RequestBody @Valid TrainerActionDto trainerActionDto) {
        trainerWorkloadService.updateTrainerWorkload(trainerActionDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer's monthly workload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the workload")
    })
    public ResponseEntity<TrainerWorkloadDto> getTrainerWorkload(
            @PathVariable("username") String username,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ResponseEntity.ok(trainerWorkloadService.getTrainerWorkload(username,year,month));
    }
}
