package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.entity.Answer;
import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.QuestionType;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.exception.QuestionNotFoundException;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuestionMapper;
import com.aidannolan.quizbuilder.mapper.AnswerMapper;
import com.aidannolan.quizbuilder.repository.QuestionRepository;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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

    @Mock
    private AnswerMapper answerMapper;

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

    @Test
    void shouldGetQuestionsByQuizId() {
        Question question1 = new Question();
        question1.setId(10L);
        question1.setQuestionText("First question");
        question1.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question1.setPosition(1);

        Question question2 = new Question();
        question2.setId(11L);
        question2.setQuestionText("Second question");
        question2.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question2.setPosition(2);

        QuestionResponseDTO response1 = new QuestionResponseDTO(
                10L,
                1L,
                "First question",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(),
                null,
                null
        );

        QuestionResponseDTO response2 = new QuestionResponseDTO(
                11L,
                1L,
                "Second question",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                2,
                List.of(),
                null,
                null
        );

        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        when(questionRepository.findByQuizIdOrderByPositionAsc(1L))
                .thenReturn(List.of(question1, question2));

        when(questionMapper.toResponseDTO(question1))
                .thenReturn(response1);

        when(questionMapper.toResponseDTO(question2))
                .thenReturn(response2);

        List<QuestionResponseDTO> result =
                questionService.getQuestionsByQuizId(1L);

        assertThat(result)
            .containsExactly(response1, response2);

        verify(quizRepository).findById(1L);
        verify(questionRepository)
                .findByQuizIdOrderByPositionAsc(1L);
        verify(questionMapper).toResponseDTO(question1);
        verify(questionMapper).toResponseDTO(question2);
    }

    @Test
    void shouldReturnEmptyListWhenQuizHasNoQuestions() {
        when(quizRepository.findById(1L))
                .thenReturn(Optional.of(quiz));

        when(questionRepository.findByQuizIdOrderByPositionAsc(1L))
                .thenReturn(List.of());

        List<QuestionResponseDTO> result =
                questionService.getQuestionsByQuizId(1L);

        assertThat(result).isEmpty();

        verify(quizRepository).findById(1L);
        verify(questionRepository).findByQuizIdOrderByPositionAsc(1L);
        verifyNoInteractions(questionMapper);
    }

    @Test
    void shouldThrowExceptionWhenGettingQuestionsForNonexistentQuiz() {
        when(quizRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> questionService.getQuestionsByQuizId(999L)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verifyNoInteractions(questionRepository);
        verifyNoInteractions(questionMapper);
    }

    @Test
    void shouldGetQuestionByIdWhenQuestionBelongsToQuiz() {
        Long quizId = 1L;
        Long questionId = 10L;

        Question question = new Question();
        question.setId(questionId);
        question.setQuestionText(
                "Which keyword is used to inherit from a class?"
        );
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question.setPosition(1);

        QuestionResponseDTO response = new QuestionResponseDTO(
                questionId,
                quizId,
                "Which keyword is used to inherit from a class?",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(),
                null,
                null
        );

        when(questionRepository.findByIdAndQuizId(
                questionId,
                quizId
        )).thenReturn(Optional.of(question));

        when(questionMapper.toResponseDTO(question))
                .thenReturn(response);

        QuestionResponseDTO result =
                questionService.getQuestionById(
                        quizId,
                        questionId
                );

        assertThat(result).isEqualTo(response);

        verify(questionRepository)
                .findByIdAndQuizId(questionId, quizId);

        verify(questionMapper)
                .toResponseDTO(question);
    }

    @Test
    void shouldThrowExceptionWhenQuestionDoesNotExist() {
        Long quizId = 1L;
        Long questionId = 10L;

        when(questionRepository.findByIdAndQuizId(
                questionId,
                quizId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> questionService.getQuestionById(
                        quizId,
                        questionId
                )
        )
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessage("Question not found: " + questionId);

        verify(questionRepository)
                .findByIdAndQuizId(questionId, quizId);

        verifyNoInteractions(questionMapper);
    }

    @Test
    void shouldThrowExceptionWhenQuestionDoesNotBelongToQuiz() {
        Long requestedQuizId = 1L;
        Long questionId = 10L;

        when(questionRepository.findByIdAndQuizId(
                questionId,
                requestedQuizId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> questionService.getQuestionById(
                        requestedQuizId,
                        questionId
                )
        )
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessage("Question not found: " + questionId);

        verify(questionRepository)
                .findByIdAndQuizId(
                        questionId,
                        requestedQuizId
                );

        verifyNoInteractions(questionMapper);
    }

    @Test
    void shouldUpdateQuestionAndReplaceAnswers() {
        Long quizId = 1L;
        Long questionId = 10L;

        Question existingQuestion = new Question();
        existingQuestion.setId(questionId);
        existingQuestion.setQuestionText("Old question");
        existingQuestion.setQuestionType(
                QuestionType.MULTIPLE_CHOICE_SINGLE
        );
        existingQuestion.setPosition(1);

        Answer oldAnswer1 = new Answer();
        oldAnswer1.setId(100L);
        oldAnswer1.setQuestion(existingQuestion);
        oldAnswer1.setAnswerText("Old answer 1");
        oldAnswer1.setCorrect(true);
        oldAnswer1.setPosition(1);

        Answer oldAnswer2 = new Answer();
        oldAnswer2.setId(101L);
        oldAnswer2.setQuestion(existingQuestion);
        oldAnswer2.setAnswerText("Old answer 2");
        oldAnswer2.setCorrect(false);
        oldAnswer2.setPosition(2);

        existingQuestion.setAnswers(
                new ArrayList<>(List.of(oldAnswer1, oldAnswer2))
        );

        QuestionRequestDTO request = new QuestionRequestDTO(
                "Updated question",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                2,
                List.of(
                        new AnswerRequestDTO(
                                "New correct answer",
                                true,
                                1
                        ),
                        new AnswerRequestDTO(
                                "New incorrect answer",
                                false,
                                2
                        )
                )
        );

        Question updatedQuestion = new Question();
        updatedQuestion.setId(questionId);
        updatedQuestion.setQuiz(quiz);
        updatedQuestion.setQuestionText("Updated question");
        updatedQuestion.setQuestionType(
                QuestionType.MULTIPLE_CHOICE_SINGLE
        );
        updatedQuestion.setPosition(2);

        Answer newAnswer1 = new Answer();
        newAnswer1.setAnswerText("New correct answer");
        newAnswer1.setCorrect(true);
        newAnswer1.setPosition(1);

        Answer newAnswer2 = new Answer();
        newAnswer2.setAnswerText("New incorrect answer");
        newAnswer2.setCorrect(false);
        newAnswer2.setPosition(2);

        QuestionResponseDTO response = new QuestionResponseDTO(
                questionId,
                quizId,
                "Updated question",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                2,
                List.of(),
                null,
                null
        );

        when(questionRepository.findByIdAndQuizId(
                questionId,
                quizId
        )).thenReturn(Optional.of(existingQuestion));

        doAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            QuestionRequestDTO dto = invocation.getArgument(1);

            question.setQuestionText(dto.questionText());
            question.setQuestionType(dto.questionType());
            question.setPosition(dto.position());

            return null;
        }).when(questionMapper)
                        .updateEntity(existingQuestion, request);

        when(questionRepository.save(existingQuestion))
                .thenReturn(existingQuestion);

        when(questionMapper.toResponseDTO(existingQuestion))
                .thenReturn(response);

        when(answerMapper.toEntity(request.answers().get(0)))
                .thenReturn(newAnswer1);

        when(answerMapper.toEntity(request.answers().get(1)))
                .thenReturn(newAnswer2);

        questionService.updateQuestion(
            quizId,
            questionId,
            request
        );

        verify(questionMapper)
                .updateEntity(existingQuestion, request);

        verify(questionRepository)
                .save(existingQuestion);

        verify(questionMapper)
                .toResponseDTO(existingQuestion);

        assertThat(existingQuestion.getQuestionText())
            .isEqualTo("Updated question");

        assertThat(existingQuestion.getPosition())
            .isEqualTo(2);

        assertThat(existingQuestion.getAnswers())
            .hasSize(2);

        assertThat(existingQuestion.getAnswers())
            .extracting(Answer::getAnswerText)
            .containsExactly(
                "New correct answer",
                "New incorrect answer"
            );

        assertThat(existingQuestion.getAnswers())
            .allSatisfy(answer ->
                assertThat(answer.getQuestion())
                    .isSameAs(existingQuestion)
            );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingQuestionThatDoesNotExist() {
        Long quizId = 1L;
        Long questionId = 999L;

        QuestionRequestDTO request = new QuestionRequestDTO(
                "Updated question",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(
                        new AnswerRequestDTO(
                                "Answer",
                                true,
                                1
                        )
                )
        );

        when(questionRepository.findByIdAndQuizId(
                questionId,
                quizId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> questionService.updateQuestion(
                        quizId,
                        questionId,
                        request
                )
        )
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessage("Question not found: " + questionId);

        verify(questionRepository)
                .findByIdAndQuizId(questionId, quizId);

        verifyNoInteractions(questionMapper);
    }
}
