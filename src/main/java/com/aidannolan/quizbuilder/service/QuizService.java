package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;

import java.util.List;

public interface QuizService {
    QuizResponseDTO createQuiz(QuizRequestDTO quiz);

    QuizResponseDTO getQuizById(Long id);

    List<QuizResponseDTO> getQuizzesByOwnerId(Long ownerId);

    QuizResponseDTO updateQuiz(Long id, QuizRequestDTO quiz);

    void deleteQuiz(Long id);
}
