package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizUpdateRequestDTO;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.exception.UserNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuizMapper;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import com.aidannolan.quizbuilder.repository.UserRepository;
import com.aidannolan.quizbuilder.service.QuizService;
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
        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        return quizRepository.findByOwnerId(ownerId)
                .stream()
                .map(quizMapper::toResponseDTO)
                .toList();
    }

    @Override
    public QuizResponseDTO updateQuiz(Long id, QuizUpdateRequestDTO request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setStatus(request.status());

        Quiz updatedQuiz = quizRepository.save(quiz);

        return quizMapper.toResponseDTO(updatedQuiz);
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        quizRepository.delete(existingQuiz);
    }
}
