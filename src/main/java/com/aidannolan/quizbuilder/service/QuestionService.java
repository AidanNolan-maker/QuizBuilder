package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;

import java.util.List;

public interface QuestionService {
    QuestionResponseDTO createQuestion(
            Long quizId,
            QuestionRequestDTO request
    );

    List<QuestionResponseDTO> getQuestionsByQuizId(Long quizId);

    QuestionResponseDTO getQuestionById(Long quizId, Long questionId);
}
