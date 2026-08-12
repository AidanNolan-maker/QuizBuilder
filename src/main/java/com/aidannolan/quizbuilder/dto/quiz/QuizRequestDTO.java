package com.aidannolan.quizbuilder.dto.quiz;

import com.aidannolan.quizbuilder.entity.QuizStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuizRequestDTO(
        @NotNull
        Long ownerId,

        @NotBlank
        @Size(max = 150)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull
        QuizStatus status
) {
}
