package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.LoginRequestDTO;
import com.aidannolan.quizbuilder.dto.LoginResponseDTO;
import com.aidannolan.quizbuilder.dto.RegisterRequestDTO;
import com.aidannolan.quizbuilder.dto.UserResponseDTO;
import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.exception.DuplicateEmailException;
import com.aidannolan.quizbuilder.exception.DuplicateUsernameException;
import com.aidannolan.quizbuilder.exception.InvalidCredentialsException;
import com.aidannolan.quizbuilder.repository.UserRepository;
import com.aidannolan.quizbuilder.service.JwtService;
import com.aidannolan.quizbuilder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserResponseDTO registerUser(RegisterRequestDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateUsernameException(request.username());
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateEmailException(request.email());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        User savedUser =  userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                    new InvalidCredentialsException()
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(
                user.getUsername()
        );

        return new LoginResponseDTO(token);
    }
}
