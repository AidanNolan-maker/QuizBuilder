package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.entity.Quiz;

import java.util.List;

public interface QuizService {
    Quiz createQuiz(Quiz quiz);

    Quiz getQuizById(Long id);

    List<Quiz> getQuizzesByOwnerId(Long ownerId);

    Quiz updateQuiz(Long id, Quiz quiz);

    void deleteQuiz(Long id);
}
