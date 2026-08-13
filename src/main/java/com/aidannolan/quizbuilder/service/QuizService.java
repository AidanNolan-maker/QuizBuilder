package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizUpdateRequestDTO;

import java.util.List;

public interface QuizService {
    QuizResponseDTO createQuiz(QuizRequestDTO quiz);

    QuizResponseDTO getQuizById(Long id);

    List<QuizResponseDTO> getQuizzesByOwnerId(Long ownerId);

    void deleteQuiz(Long id);

    QuizResponseDTO updateQuiz(Long id, QuizUpdateRequestDTO request);
}
