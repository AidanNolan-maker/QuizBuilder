package com.aidannolan.quizbuilder.dto.quiz;

import com.aidannolan.quizbuilder.entity.QuizStatus;

import java.time.LocalDateTime;

public record QuizResponseDTO(
        Long id,
        Long ownerId,
        String title,
        String description,
        QuizStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
