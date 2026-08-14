package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.entity.Answer;
import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuestionMapper;
import com.aidannolan.quizbuilder.repository.QuestionRepository;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import com.aidannolan.quizbuilder.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public QuestionResponseDTO createQuestion(
            Long quizId,
            QuestionRequestDTO request
    ) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        Question question = questionMapper.toEntity(request);

        question.setQuiz(quiz);

        for (Answer answer : question.getAnswers()) {
            answer.setQuestion(question);
        }

        Question savedQuestion = questionRepository.save(question);

        return questionMapper.toResponseDTO(savedQuestion);
    }

    @Override
    @Transactional
    public List<QuestionResponseDTO> getQuestionsByQuizId(Long quizId) {
        quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        return questionRepository
                .findByQuizIdOrderByPositionAsc(quizId)
                .stream()
                .map(questionMapper::toResponseDTO)
                .toList();
    }
}
