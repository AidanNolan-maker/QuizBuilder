package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.entity.Answer;
import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.exception.QuestionNotFoundException;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuestionMapper;
import com.aidannolan.quizbuilder.mapper.AnswerMapper;
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
    private final AnswerMapper answerMapper;

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

    @Override
    @Transactional
    public QuestionResponseDTO getQuestionById(Long quizId, Long questionId) {
        Question question = questionRepository.findByIdAndQuizId(questionId, quizId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        return questionMapper.toResponseDTO(question);
    }

    @Override
    @Transactional
    public QuestionResponseDTO updateQuestion(
            Long quizId,
            Long questionId,
            QuestionRequestDTO request
    ) {
        Question question = questionRepository
                .findByIdAndQuizId(questionId, quizId)
                .orElseThrow(
                        () -> new QuestionNotFoundException(questionId)
                );

        questionMapper.updateEntity(question, request);

        question.getAnswers().clear();

        List<Answer> newAnswers = request.answers()
                .stream()
                .map(answerMapper::toEntity)
                .toList();

        for (Answer answer : newAnswers) {
            answer.setQuestion(question);
        }

        question.getAnswers().addAll(newAnswers);

        Question savedQuestion = questionRepository.save(question);

        return questionMapper.toResponseDTO(savedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long quizId, Long questionId) {
        Question question = questionRepository
                .findByIdAndQuizId(questionId, quizId)
                .orElseThrow(
                        () -> new QuestionNotFoundException(questionId)
                );

        questionRepository.delete(question);
    }
}
