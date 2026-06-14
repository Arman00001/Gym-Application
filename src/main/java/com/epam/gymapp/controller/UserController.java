package com.epam.gymapp.controller;

import com.epam.gymapp.dto.user.ChangePasswordRequestDto;
import com.epam.gymapp.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", produces = {"application/JSON"})
@Tag(name = "Users", description = "Operations for logging in the user, changing credentials in the application")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Logged in successfully")
    public ResponseEntity<String> login(
            @RequestParam("username") @NotBlank String username,
            @RequestParam("password") @NotBlank String password
    ){
        if(userService.login(username, password)){
            return ResponseEntity.ok("Login successful");
        }

        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @PutMapping
    @Operation(summary = "Change Password")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    public ResponseEntity<String> changePassword(@Valid ChangePasswordRequestDto dto){
        userService.changePassword(dto);
        return ResponseEntity.ok("Password changed successfully");
    }
}
