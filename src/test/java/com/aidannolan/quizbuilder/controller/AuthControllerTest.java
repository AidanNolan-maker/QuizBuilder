package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.RegisterRequestDTO;
import com.aidannolan.quizbuilder.dto.UserResponseDTO;
import com.aidannolan.quizbuilder.dto.LoginRequestDTO;
import com.aidannolan.quizbuilder.dto.LoginResponseDTO;
import com.aidannolan.quizbuilder.exception.InvalidCredentialsException;
import com.aidannolan.quizbuilder.exception.DuplicateEmailException;
import com.aidannolan.quizbuilder.exception.DuplicateUsernameException;
import com.aidannolan.quizbuilder.service.UserService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
@WithMockUser
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldRegisterUser() throws Exception {
        UserResponseDTO response = new UserResponseDTO(
                1L,
                "aidan",
                "aidan@example.com"
        );

        when(userService.registerUser(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                    post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "aidan",
                                        "email": "aidan@example.com",
                                        "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("aidan"))
                .andExpect(jsonPath("$.email")
                        .value("aidan@example.com"));
    }

    @Test
    void shouldReturnConflictWhenUsernameAlreadyExists() throws Exception {
        when(userService.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(
                        new DuplicateUsernameException("aidan")
                );

        mockMvc.perform(
                    post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "aidan",
                                        "email": "aidan@example.com",
                                        "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Username Already Exists"))
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.detail")
                        .value("Username already exists: aidan"));
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(userService.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(
                        new DuplicateEmailException("aidan@example.com")
                );

        mockMvc.perform(
                    post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "aidan",
                                        "email": "aidan@example.com",
                                        "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Email Already Exists"))
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Email already exists: aidan@example.com"
                        ));
    }

    @Test
    void shouldRejectInvalidRegistration() throws Exception {
        mockMvc.perform(
                    post("/api/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "",
                                        "email": "not-an-email",
                                        "password": "123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginResponseDTO response =
                new LoginResponseDTO("jwt-token");

        when(userService.loginUser(any(LoginRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                    post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "aidan",
                                        "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("jwt-token"));
    }

    @Test
    void shouldReturnUnauthorizedWhenUsernameDoesNotExist()  throws Exception {
        when(userService.loginUser(any(LoginRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(
                    post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "unknown",
                                        "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Credentials"))
                .andExpect(jsonPath("$.status")
                        .value(401))
                .andExpect(jsonPath("$.detail")
                        .value("Invalid username or password"));
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws Exception {
        when(userService.loginUser(any(LoginRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(
                    post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "aidan",
                                        "password": "wrongPassword"
                                    }
                                    """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Credentials"))
                .andExpect(jsonPath("$.status")
                        .value(401))
                .andExpect(jsonPath("$.detail")
                        .value("Invalid username or password"));
    }

    @Test
    void shouldRejectInvalidLoginRequest() throws Exception {
        mockMvc.perform(
                    post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "username": "",
                                        "password": ""
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
