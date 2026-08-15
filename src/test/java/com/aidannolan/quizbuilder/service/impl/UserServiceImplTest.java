package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.RegisterRequestDTO;
import com.aidannolan.quizbuilder.dto.UserResponseDTO;
import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.exception.DuplicateEmailException;
import com.aidannolan.quizbuilder.exception.DuplicateUsernameException;
import com.aidannolan.quizbuilder.repository.UserRepository;
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
import static org.mockito.Mockito.never;
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
}
