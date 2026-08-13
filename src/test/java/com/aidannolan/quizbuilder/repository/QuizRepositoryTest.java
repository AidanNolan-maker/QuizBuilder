package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.*;
import com.aidannolan.quizbuilder.config.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuizRepositoryTest {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void shouldSaveQuizWithOwner() {
        User user = new User();
        user.setUsername("quizcreator");
        user.setEmail("quizcreator@example.com");
        user.setPasswordHash("temporary-test-hash");

        User savedUser = userRepository.saveAndFlush(user);

        Quiz quiz = new Quiz();
        quiz.setOwner(savedUser);
        quiz.setTitle("Java Fundamentals");
        quiz.setDescription("A quiz covering basic Java concepts.");
        quiz.setStatus(QuizStatus.DRAFT);

        Quiz savedQuiz = quizRepository.saveAndFlush(quiz);

        assertThat(savedQuiz.getId()).isNotNull();
        assertThat(savedQuiz.getOwner().getId())
                .isEqualTo(savedUser.getId());
        assertThat(savedQuiz.getTitle())
                .isEqualTo("Java Fundamentals");
        assertThat(savedQuiz.getStatus())
            .isEqualTo(QuizStatus.DRAFT);
        assertThat(savedQuiz.getCreatedAt()).isNotNull();
        assertThat(savedQuiz.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindQuizzesByOwnerId() {
        User user = new User();
        user.setUsername("quizcreator");
        user.setEmail("quizcreator@example.com");
        user.setPasswordHash("temporary-test-hash");

        User savedUser = userRepository.saveAndFlush(user);

        Quiz quiz = new Quiz();
        quiz.setOwner(savedUser);
        quiz.setTitle("Java Fundamentals");
        quiz.setDescription("A quiz covering basic Java concepts.");
        quiz.setStatus(QuizStatus.DRAFT);

        quizRepository.saveAndFlush(quiz);

        var quizzes = quizRepository.findByOwnerId(savedUser.getId());

        assertThat(quizzes).hasSize(1);
        assertThat(quizzes.getFirst().getTitle())
                .isEqualTo("Java Fundamentals");
    }

    @Test
    void shouldDeleteQuizAndCascadeToQuestionsAndAnswers() {
        User user = new User();
        user.setUsername("cascade-test-user");
        user.setEmail("cascade@example.com");
        user.setPasswordHash("teporary-test-hash");

        userRepository.save(user);

        Quiz quiz = new Quiz();
        quiz.setOwner(user);
        quiz.setTitle("Cascade Type Quiz");
        quiz.setDescription("Testing cascade deletion.");
        quiz.setStatus(QuizStatus.DRAFT);

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText("What is 2 + 2?");
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question.setPosition(1);

        Answer correctAnswer = new Answer();
        correctAnswer.setQuestion(question);
        correctAnswer.setAnswerText("4");
        correctAnswer.setCorrect(true);
        correctAnswer.setPosition(1);

        Answer incorrectAnswer = new Answer();
        incorrectAnswer.setQuestion(question);
        incorrectAnswer.setAnswerText("5");
        incorrectAnswer.setCorrect(false);
        incorrectAnswer.setPosition(2);

        question.setAnswers(
                new ArrayList<>(List.of(correctAnswer, incorrectAnswer))
        );

        quiz.setQuestions(
                new ArrayList<>(List.of(question))
        );

        quizRepository.saveAndFlush(quiz);

        Long quizId = quiz.getId();
        Long questionId = question.getId();
        Long correctAnswerId = correctAnswer.getId();
        Long incorrectAnswerId = incorrectAnswer.getId();

        quizRepository.deleteById(quizId);
        quizRepository.flush();

        assertThat(quizRepository.findById(quizId))
                .isEmpty();

        assertThat(questionRepository.findById(questionId))
                .isEmpty();

        assertThat(answerRepository.findById(correctAnswerId))
                .isEmpty();

        assertThat(answerRepository.findById(incorrectAnswerId))
                .isEmpty();
    }
}
