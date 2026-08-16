package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.LoginRequestDTO;
import com.aidannolan.quizbuilder.dto.LoginResponseDTO;
import com.aidannolan.quizbuilder.dto.RegisterRequestDTO;
import com.aidannolan.quizbuilder.dto.UserResponseDTO;
import com.aidannolan.quizbuilder.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        return userService.loginUser(request);
    }
}
