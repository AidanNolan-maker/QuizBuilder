package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.dto.RegisterRequestDTO;
import com.aidannolan.quizbuilder.dto.UserResponseDTO;
import com.aidannolan.quizbuilder.dto.LoginRequestDTO;
import com.aidannolan.quizbuilder.dto.LoginResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(RegisterRequestDTO request);

    LoginResponseDTO loginUser(LoginRequestDTO request);
}
