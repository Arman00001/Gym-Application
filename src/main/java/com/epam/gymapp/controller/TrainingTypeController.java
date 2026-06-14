package com.epam.gymapp.controller;

import com.epam.gymapp.dto.trainingtype.TrainingTypeDto;
import com.epam.gymapp.service.trainingtype.TrainingTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/training-types", produces = {"application/JSON"})
@Tag(name = "Training Types", description = "Operations for retrieving training types in the application")
@RequiredArgsConstructor
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @GetMapping("/all")
    public ResponseEntity<List<TrainingTypeDto>> getAll() {
        return ResponseEntity.ok(trainingTypeService.getAll());
    }
}
