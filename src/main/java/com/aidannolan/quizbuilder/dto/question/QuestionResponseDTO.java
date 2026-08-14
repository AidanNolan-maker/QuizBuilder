package com.aidannolan.quizbuilder.dto.question;

import com.aidannolan.quizbuilder.dto.answer.AnswerResponseDTO;
import com.aidannolan.quizbuilder.entity.QuestionType;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionResponseDTO(
        Long id,
        Long quizId,
        String questionText,
        QuestionType questionType,
        Integer position,
        List<AnswerResponseDTO> answers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
