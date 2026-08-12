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
}
