package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.entity.Answer;
import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.QuestionType;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuestionMapper;
import com.aidannolan.quizbuilder.repository.QuestionRepository;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Quiz quiz;
    private Question question;
    private QuestionResponseDTO response;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Java Fundamentals");

        question = new Question();
        question.setId(10L);
        question.setQuestionText(
                "Which keyword is used to inherit from a class?"
        );
        question.setQuestionType(
                QuestionType.MULTIPLE_CHOICE_SINGLE
        );
        question.setPosition(1);

        Answer correctAnswer = new Answer();
        correctAnswer.setId(100L);
        correctAnswer.setAnswerText("extends");
        correctAnswer.setCorrect(true);
        correctAnswer.setPosition(1);

        Answer incorrectAnswer = new Answer();
        incorrectAnswer.setId(101L);
        incorrectAnswer.setAnswerText("implements");
        incorrectAnswer.setCorrect(false);
        incorrectAnswer.setPosition(2);

        question.setAnswers(
                List.of(correctAnswer, incorrectAnswer)
        );

        response = new QuestionResponseDTO(
                10L,
                1L,
                "Which keyword is used to inherit from a class?",
                    QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(),
                null,
                null
        );
    }

    @Test
    void shouldCreateQuestion() {
        QuestionRequestDTO request = new QuestionRequestDTO(
                "Which keyword is used to inherit from a class?",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(
                        new AnswerRequestDTO(
                                "extends",
                                true,
                                1
                        ),
                        new AnswerRequestDTO(
                                "implements",
                                false,
                                2
                        )
                )
        );

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        when(questionMapper.toEntity(request))
                .thenReturn(question);

        when(questionRepository.save(question))
                .thenReturn(question);

        when(questionMapper.toResponseDTO(question))
                .thenReturn(response);

        QuestionResponseDTO result =
                questionService.createQuestion(1L, request);

        assertThat(result).isEqualTo(response);

        assertThat(question.getQuiz())
                .isSameAs(quiz);

        assertThat(question.getAnswers())
                .allSatisfy(answer ->
                        assertThat(answer.getQuestion())
                                .isSameAs(question)
                );

        verify(quizRepository).findById(1L);
        verify(questionMapper).toEntity(request);
        verify(questionRepository).save(question);
        verify(questionMapper).toResponseDTO(question);
    }

    @Test
    void shouldThrowExceptionWhenQuizDoesNotExist() {
        QuestionRequestDTO request = new QuestionRequestDTO(
                "Which keyword is used to inherit from a class?",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(
                        new AnswerRequestDTO(
                                "extends",
                                true,
                                1
                        )
                )
        );

        when(quizRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> questionService.createQuestion(999L, request)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verify(quizRepository).findById(999L);
        verifyNoInteractions(questionMapper);
        verifyNoInteractions(questionRepository);
    }
}
