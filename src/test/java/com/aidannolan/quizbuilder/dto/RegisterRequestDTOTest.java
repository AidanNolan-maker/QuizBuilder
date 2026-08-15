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

public class RegisterRequestDTOTest {
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
    void shouldAcceptValidRegistration() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "password123"
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectBlankUsername() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "",
                "aidan@example.com",
                "password123"
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("username"));
    }

    @Test
    void shouldRejectUsernameThatIsTooShort() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "ab",
                "aidan@example.com",
                "password123"
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("username"));
    }

    @Test
    void shouldRejectUsernameThatIsTooLong() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "a".repeat(51),
                "aidan@example.com",
                "password123"
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("username"));
    }

    @Test
    void shouldRejectBlankEmail() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "",
                "password123"
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("email"));
    }

    @Test
    void shouldRejectInvalidEmail() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "not-an-email",
                "password123"
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("email"));
    }

    @Test
    void shouldRejectBlankPassword() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                ""
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("password"));
    }

    @Test
    void shouldRejectPasswordThatIsTooShort() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "1234567"
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("password"));
    }

    @Test
    void shouldRejectPasswordThatIsTooLong() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "aidan",
                "aidan@example.com",
                "a".repeat(101)
        );

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals("password"));
    }
}
