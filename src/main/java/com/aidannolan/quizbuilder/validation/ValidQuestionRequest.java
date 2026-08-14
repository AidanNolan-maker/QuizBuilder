package com.aidannolan.quizbuilder.validation;

import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.entity.QuestionType;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidQuestionRequest.Validator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidQuestionRequest {
    String message() default "Question answers are invalid";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidQuestionRequest, QuestionRequestDTO> {
        @Override
        public boolean isValid(
                QuestionRequestDTO request,
                ConstraintValidatorContext context
        ) {
            if (request == null) {
                return true;
            }

            if (request.answers() == null) {
                return true;
            }

            if (request.questionType() == QuestionType.MULTIPLE_CHOICE_SINGLE) {
                long correctAnswerCount = request.answers()
                        .stream()
                        .filter(answer -> Boolean.TRUE.equals(answer.correct()))
                        .count();

                if (correctAnswerCount != 1) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "MULTIPLE_CHOICE_SINGLE questions must have exactly one correct answer"
                            )
                            .addPropertyNode("answers")
                            .addConstraintViolation();

                    return false;
                }
            }

            return true;
        }
    }
}
