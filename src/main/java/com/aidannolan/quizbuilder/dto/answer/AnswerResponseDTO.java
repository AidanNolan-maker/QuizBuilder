package com.aidannolan.quizbuilder.dto.answer;

import java.time.LocalDateTime;

public record AnswerResponseDTO(
        Long id,
        String answerText,
        Boolean correct,
        Integer position,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
