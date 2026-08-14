package com.aidannolan.quizbuilder.dto.question;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.entity.QuestionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionRequestDTOTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptQuestionWithExactlyOneCorrectAnswer() {
        QuestionRequestDTO request = validRequest(
                List.of(
                        answer("extends", true, 1),
                        answer("implements", false, 2),
                        answer("inherits", false, 3),
                        answer("super", false, 4)
                )
        );

        Set<ConstraintViolation<QuestionRequestDTO>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectQuestionWithNoCorrectAnswers() {
        QuestionRequestDTO request = validRequest(
                List.of(
                        answer("extends", false, 1),
                        answer("implements", false, 2),
                        answer("inherits", false, 3),
                        answer("super", false, 4)
                )
        );

        Set<ConstraintViolation<QuestionRequestDTO>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getMessage().contains(
                                "exactly one correct answer"
                        )
                );
    }

    @Test
    void shouldRejectQuestionWithMultipleCorrectAnswers() {
        QuestionRequestDTO request = validRequest(
                List.of(
                        answer("extends", true, 1),
                        answer("implements", true, 2),
                        answer("inherits", false, 3),
                        answer("super", false, 4)
                )
        );

        Set<ConstraintViolation<QuestionRequestDTO>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getMessage().contains(
                                "exactly one correct answer"
                        )
                );
    }

    @Test
    void shouldRejectBlankQuestionText() {
        QuestionRequestDTO request = new QuestionRequestDTO(
                "",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(
                        answer("extends", true, 1)
                )
        );

        Set<ConstraintViolation<QuestionRequestDTO>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getMessage().equals(
                                "must not be blank"
                        )
                );
    }

    @Test
    void shouldRejectNullQuestionType() {
        QuestionRequestDTO request = new QuestionRequestDTO(
                "Which keyword is used to inherit from a class?",
                null,
                1,
                List.of(
                        answer("extends", true, 1)
                )
        );

        Set<ConstraintViolation<QuestionRequestDTO>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getMessage().equals(
                                "must not be null"
                        )
                );
    }

    @Test
    void shouldRejectEmptyAnswers() {
        QuestionRequestDTO request = new QuestionRequestDTO(
                "Which keyword is used to inherit from a class?",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of()
        );

        Set<ConstraintViolation<QuestionRequestDTO>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getMessage().equals(
                                "must not be empty"
                        )
                );
    }

    @Test
    void shouldRejectAnswerWithBlankText() {
        QuestionRequestDTO request = validRequest(
                List.of(
                        answer("", true, 1),
                        answer("implements", false, 2)
                )
        );

        Set<ConstraintViolation<QuestionRequestDTO>> violations = validator.validate(request);

        assertThat(violations)
        .anyMatch(violation ->
                    violation.getMessage().equals(
                            "must not be blank"
                    )
                );
    }

    private static QuestionRequestDTO validRequest(
            List<AnswerRequestDTO> answers
    ) {
        return new QuestionRequestDTO(
                "Which keyword is used to inherit from a class?",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                answers
        );
    }

    private static AnswerRequestDTO answer(
            String text,
            boolean correct,
            int position
    ) {
        return new AnswerRequestDTO(
                text,
                correct,
                position
        );
    }
}
