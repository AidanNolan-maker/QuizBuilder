package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    @Override
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() ->
                        new QuizNotFoundException(id));
    }

    @Override
    public List<Quiz> getQuizzesByOwnerId(Long ownerId) {
        return quizRepository.findByOwnerId(ownerId);
    }

    @Override
    public Quiz updateQuiz(Long id, Quiz quiz) {
        Quiz existingQuiz = getQuizById(id);

        existingQuiz.setTitle(quiz.getTitle());
        existingQuiz.setDescription(quiz.getDescription());
        existingQuiz.setStatus(quiz.getStatus());

        return quizRepository.save(existingQuiz);
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz existingQuiz = getQuizById(id);
        quizRepository.delete(existingQuiz);
    }
}
