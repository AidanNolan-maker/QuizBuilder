package com.aidannolan.quizbuilder.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthenticationServiceImplTest {
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService =
                new AuthenticationServiceImpl();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUsername() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "aidan",
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        String username =
                authenticationService.getCurrentUsername();

        assertThat(username)
                .isEqualTo("aidan");
    }

    @Test
    void shouldRejectWhenNoAuthenticationExists() {
        assertThatThrownBy(
                () -> authenticationService.getCurrentUsername()
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found");
    }

    @Test
    void shouldRejectWhenAuthenticationIsNotAuthenticated() {
        Authentication authentication =
                org.mockito.Mockito.mock(Authentication.class);

        org.mockito.Mockito.when(authentication.isAuthenticated())
                .thenReturn(false);

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        assertThatThrownBy(
                () -> authenticationService.getCurrentUsername()
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found");
    }
}
