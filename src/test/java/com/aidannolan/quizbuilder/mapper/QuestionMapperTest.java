package com.aidannolan.quizbuilder.mapper;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.entity.Answer;
import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.QuestionType;
import com.aidannolan.quizbuilder.entity.Quiz;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionMapperTest {
    private final AnswerMapper answerMapper = new AnswerMapper();
    private final QuestionMapper questionMapper = new QuestionMapper(answerMapper);

    @Test
    void shouldMapRequestToEntity() {
        QuestionRequestDTO dto = new QuestionRequestDTO(
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

        Question question = questionMapper.toEntity(dto);

        assertThat(question.getId()).isNull();
        assertThat(question.getQuestionText())
                .isEqualTo(
                        "Which keyword is used to inherit from a class?"
                );
        assertThat(question.getQuestionType())
                .isEqualTo(QuestionType.MULTIPLE_CHOICE_SINGLE);
        assertThat(question.getPosition()).isEqualTo(1);

        assertThat(question.getAnswers())
                .hasSize(2);

        assertThat(question.getAnswers().get(0).getAnswerText())
                .isEqualTo("extends");

        assertThat(question.getAnswers().get(0).isCorrect())
                .isTrue();

        assertThat(question.getAnswers().get(1).getAnswerText())
                .isEqualTo("implements");

        assertThat(question.getAnswers().get(1).isCorrect())
                .isFalse();

        assertThat(question.getQuiz()).isNull();

        assertThat(question.getAnswers().get(0).getQuestion())
                .isNull();
    }

    @Test
    void shouldMapEntityToResponseDTO() {
        Quiz quiz = new Quiz();
        quiz.setId(10L);

        Question question = new Question();
        question.setId(1L);
        question.setQuiz(quiz);
        question.setQuestionText(
                "Which keyword is used to inherit from a class?"
        );
        question.setQuestionType(
                QuestionType.MULTIPLE_CHOICE_SINGLE
        );
        question.setPosition(1);

        Answer answer1 = new Answer();
        answer1.setId(100L);
        answer1.setQuestion(question);
        answer1.setAnswerText("extends");
        answer1.setCorrect(true);
        answer1.setPosition(1);

        Answer answer2 = new Answer();
        answer2.setId(101L);
        answer2.setQuestion(question);
        answer2.setAnswerText("implements");
        answer2.setCorrect(false);
        answer2.setPosition(2);

        question.setAnswers(
                List.of(answer1, answer2)
        );

        QuestionResponseDTO result =
                questionMapper.toResponseDTO(question);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.quizId()).isEqualTo(10L);
        assertThat(result.questionText())
                .isEqualTo(
                        "Which keyword is used to inherit from a class?"
                );
        assertThat(result.questionType())
                .isEqualTo(QuestionType.MULTIPLE_CHOICE_SINGLE);
        assertThat(result.position()).isEqualTo(1);

        assertThat(result.answers())
                .hasSize(2);

        assertThat(result.answers().get(0).id())
                .isEqualTo(100L);

        assertThat(result.answers().get(0).answerText())
                .isEqualTo("extends");

        assertThat(result.answers().get(0).correct())
                .isTrue();

        assertThat(result.answers().get(1).id())
                .isEqualTo(101L);

        assertThat(result.answers().get(1).answerText())
                .isEqualTo("implements");

        assertThat(result.answers().get(1).correct())
                .isFalse();
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        Quiz quiz = new Quiz();
        quiz.setId(5L);

        Question question = new Question();
        question.setId(10L);
        question.setQuiz(quiz);
        question.setQuestionText("Old question");
        question.setQuestionType(
                QuestionType.MULTIPLE_CHOICE_SINGLE
        );
        question.setPosition(1);

        Answer existingAnswer = new Answer();
        existingAnswer.setId(100L);
        existingAnswer.setQuestion(question);
        existingAnswer.setAnswerText("Old answer");
        existingAnswer.setCorrect(true);
        existingAnswer.setPosition(1);

        question.setAnswers(
                new ArrayList<>(List.of(existingAnswer))
        );

        QuestionRequestDTO dto = new QuestionRequestDTO(
                "Updated question",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                2,
                List.of(
                        new AnswerRequestDTO(
                                "New answer",
                                true,
                                1
                        )
                )
        );

        questionMapper.updateEntity(question, dto);

        assertThat(question.getId())
                .isEqualTo(10L);

        assertThat(question.getQuiz())
                .isSameAs(quiz);

        assertThat(question.getQuestionText())
                .isEqualTo("Updated question");

        assertThat(question.getQuestionType())
                .isEqualTo(QuestionType.MULTIPLE_CHOICE_SINGLE);

        assertThat(question.getPosition())
                .isEqualTo(2);

        assertThat(question.getAnswers())
                .hasSize(1);

        assertThat(question.getAnswers().get(0))
                .isSameAs(existingAnswer);

        assertThat(question.getAnswers().get(0).getAnswerText())
                .isEqualTo("Old answer");
    }
}
