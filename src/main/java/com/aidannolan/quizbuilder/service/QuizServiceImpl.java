package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.exception.UserNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuizMapper;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import com.aidannolan.quizbuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizMapper quizMapper;

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO request) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() ->
                        new UserNotFoundException(request.ownerId()));

        Quiz quiz = new Quiz();
        quiz.setOwner(owner);
        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setStatus(request.status());

        Quiz savedQuiz = quizRepository.save(quiz);

        return quizMapper.toResponseDTO(savedQuiz);

    }

    @Override
    public QuizResponseDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        return quizMapper.toResponseDTO(quiz);
    }

    @Override
    public List<QuizResponseDTO> getQuizzesByOwnerId(Long ownerId) {
        return quizRepository.findByOwnerId(ownerId)
                .stream()
                .map(quizMapper::toResponseDTO)
                .toList();
    }

    @Override
    public QuizResponseDTO updateQuiz(Long id, QuizRequestDTO request) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + request.ownerId()
                ));

        existingQuiz.setOwner(owner);
        existingQuiz.setTitle(request.title());
        existingQuiz.setDescription(request.description());
        existingQuiz.setStatus(request.status());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);

        return quizMapper.toResponseDTO(updatedQuiz);
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        quizRepository.delete(existingQuiz);
    }
}
