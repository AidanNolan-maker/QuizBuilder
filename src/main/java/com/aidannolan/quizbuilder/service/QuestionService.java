package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;

public interface QuestionService {
    QuestionResponseDTO createQuestion(
            Long quizId,
            QuestionRequestDTO request
    );
}
