package com.aidannolan.quizbuilder.dto.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnswerRequestDTO(
        @NotBlank
        @Size(max = 500)
        String answerText,

        @NotNull
        Boolean correct,

        @NotNull
        Integer position
) {
}
