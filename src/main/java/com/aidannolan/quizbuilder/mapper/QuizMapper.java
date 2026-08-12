package com.aidannolan.quizbuilder.mapper;

import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.entity.Quiz;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {
    public QuizResponseDTO toResponseDTO(Quiz quiz) {
        return new QuizResponseDTO(
                quiz.getId(),
                quiz.getOwner().getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getStatus(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt()
        );
    }
}
