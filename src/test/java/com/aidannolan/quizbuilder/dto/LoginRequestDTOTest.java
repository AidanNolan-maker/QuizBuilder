package com.aidannolan.quizbuilder.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginRequestDTOTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory =
                Validation.buildDefaultValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidLoginRequest() {
        LoginRequestDTO request = new LoginRequestDTO(
                "aidan",
                "password123"
        );

        Set<ConstraintViolation<LoginRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectBlankUsername() {
        LoginRequestDTO request = new LoginRequestDTO(
                "",
                "password123"
        );

        Set<ConstraintViolation<LoginRequestDTO>> violations =
            validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("username"));
    }

    @Test
    void shouldRejectBlankPassword() {
        LoginRequestDTO request = new LoginRequestDTO(
                "aidan",
                ""
        );

        Set<ConstraintViolation<LoginRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("password"));
    }

    @Test
    void shouldRejectBlankUsernameAndPassword() {
        LoginRequestDTO request = new LoginRequestDTO(
                "",
                ""
        );

        Set<ConstraintViolation<LoginRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .hasSize(2)
                .allMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("username")
                                || violation.getPropertyPath()
                                .toString()
                                .equals("password"));
    }
}
