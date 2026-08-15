package com.aidannolan.quizbuilder.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleQuizNotFoundException() {
        QuizNotFoundException exception =
                new QuizNotFoundException(999L);

        ProblemDetail result =
                handler.handleQuizNotFound(exception);

        assertThat(result.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(result.getTitle())
                .isEqualTo("Quiz Not Found");

        assertThat(result.getDetail())
                .isEqualTo("Quiz not found: 999");
    }

    @Test
    void shouldHandleUserNotFoundException() {
        UserNotFoundException exception =
                new UserNotFoundException(999L);

        ProblemDetail result =
                handler.handleUserNotFound(exception);

        assertThat(result.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(result.getTitle())
            .isEqualTo("User Not Found");

        assertThat(result.getDetail())
                .isEqualTo("User not found: 999");
    }

    @Test
    void shouldHandleDuplicateUsernameException() {
        DuplicateUsernameException exception =
                new DuplicateUsernameException("aidan");

        ProblemDetail response =
                handler.handleDuplicateUsername(exception);

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.CONFLICT.value());

        assertThat(response.getTitle())
                .isEqualTo("Username Already Exists");

        assertThat(response.getDetail())
                .isEqualTo("Username already exists: aidan");
    }

    @Test
    void shouldHandleDuplicateEmailException() {
        DuplicateEmailException exception =
                new DuplicateEmailException("aidan@example.com");

        ProblemDetail response =
                handler.handleDuplicateEmail(exception);

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.CONFLICT.value());

        assertThat(response.getTitle())
                .isEqualTo("Email Already Exists");

        assertThat(response.getDetail())
                .isEqualTo("Email already exists: aidan@example.com");
    }
}
