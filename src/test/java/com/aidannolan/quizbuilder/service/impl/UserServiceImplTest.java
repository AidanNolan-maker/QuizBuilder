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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldRegisterUser() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "password123"
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("aidan");
        savedUser.setEmail("aidan@example.com");
        savedUser.setPasswordHash("encoded-password");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("aidan@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDTO result =
                userService.registerUser(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("aidan");
        assertThat(result.email())
                .isEqualTo("aidan@example.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldStoreEncodedPassword() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "password123"
        );

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("aidan@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getPasswordHash())
                .isEqualTo("encoded-password");

        assertThat(savedUser.getPasswordHash())
                .isNotEqualTo("password123");
    }

    @Test
    void shouldRejectDuplicateUsername() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "password123"
        );

        User existingUser = new User();
        existingUser.setUsername("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(
                () -> userService.registerUser(request)
        )
                .isInstanceOf(DuplicateUsernameException.class)
                .hasMessage("Username already exists: aidan");

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "password123"
        );

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.empty());

        User existingUser = new User();
        existingUser.setEmail("aidan@example.com");

        when(userRepository.findByEmail("aidan@example.com"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(
                () -> userService.registerUser(request)
        )
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage(
                        "Email already exists: aidan@example.com"
                );

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturnUserResponseWithoutPassword() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "password123"
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("aidan");
        savedUser.setEmail("aidan@example.com");
        savedUser.setPasswordHash("encoded-password");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("aidan@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDTO result =
                userService.registerUser(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("aidan");
        assertThat(result.email())
                .isEqualTo("aidan@example.com");
    }

    @Test
    void shouldLoginUser() {
        LoginRequestDTO request = new LoginRequestDTO(
                "aidan",
                "password123"
        );

        User user = new User();
        user.setId(1L);
        user.setUsername("aidan");
        user.setEmail("aidan@example.com");
        user.setPasswordHash("encoded-password");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encoded-password"
        )).thenReturn(true);

        when(jwtService.generateToken("aidan"))
                .thenReturn("jwt-token");

        LoginResponseDTO result =
                userService.loginUser(request);

        assertThat(result.token())
                .isEqualTo("jwt-token");

        verify(userRepository)
                .findByUsername("aidan");

        verify(passwordEncoder)
                .matches(
                        "password123",
                        "encoded-password"
                );

        verify(jwtService)
                .generateToken("aidan");
    }

    @Test
    void shouldRejectLoginWhenUsernameDoesNotExist() {
        LoginRequestDTO request = new LoginRequestDTO(
                "unknown",
                "password123"
        );

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> userService.loginUser(request)
        )
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldRejectLoginWhenPasswordIsIncorrect() {
        LoginRequestDTO request = new LoginRequestDTO(
                "aidan",
                "wrongPassword"
        );

        User user = new User();
        user.setUsername("aidan");
        user.setPasswordHash("encoded-password");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encoded-password"
        )).thenReturn(false);

        assertThatThrownBy(
                () -> userService.loginUser(request)
        )
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(passwordEncoder)
                .matches(
                        "wrongPassword",
                        "encoded-password"
                );

        verifyNoInteractions(jwtService);

        verify(jwtService, never())
                .generateToken(anyString());
    }
}
