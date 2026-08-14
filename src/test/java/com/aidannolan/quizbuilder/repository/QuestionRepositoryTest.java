package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.QuestionType;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.entity.QuizStatus;
import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.config.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QuestionRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveQuestionWithQuiz() {
        User user = new User();
        user.setUsername("questioncreator");
        user.setEmail("questioncreator@example.com");
        user.setPasswordHash("temporary-test-hash");

        User savedUser = userRepository.saveAndFlush(user);

        Quiz quiz = new Quiz();
        quiz.setOwner(savedUser);
        quiz.setTitle("Java Fundamentals");
        quiz.setDescription("A quiz covering basic Java concepts.");
        quiz.setStatus(QuizStatus.DRAFT);

        Quiz savedQuiz = quizRepository.saveAndFlush(quiz);

        Question question = new Question();
        question.setQuiz(savedQuiz);
        question.setQuestionText("Which keyword is used to inherit from a class?");
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question.setPosition(1);

        Question savedQuestion = questionRepository.saveAndFlush(question);

        assertThat(savedQuestion.getId()).isNotNull();
        assertThat(savedQuestion.getQuiz().getId())
                .isEqualTo(savedQuiz.getId());
        assertThat(savedQuestion.getQuestionText())
                .isEqualTo("Which keyword is used to inherit from a class?");
        assertThat(savedQuestion.getQuestionType())
                .isEqualTo(QuestionType.MULTIPLE_CHOICE_SINGLE);
        assertThat(savedQuestion.getPosition()).isEqualTo(1);
        assertThat(savedQuestion.getCreatedAt()).isNotNull();
        assertThat(savedQuestion.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindQuestionsByQuizIdInPositionOrder() {
        User user = new User();
        user.setUsername("questioncreator");
        user.setEmail("questioncreator@example.com");
        user.setPasswordHash("temporary-test-hash");

        User savedUser = userRepository.saveAndFlush(user);

        Quiz quiz = new Quiz();
        quiz.setOwner(savedUser);
        quiz.setTitle("Java Fundamentals");
        quiz.setDescription("A quiz covering basic Java concepts.");
        quiz.setStatus(QuizStatus.DRAFT);

        Quiz savedQuiz = quizRepository.saveAndFlush(quiz);

        Question firstQuestion = new Question();
        firstQuestion.setQuiz(savedQuiz);
        firstQuestion.setQuestionText("First question");
        firstQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        firstQuestion.setPosition(1);

        Question secondQuestion = new Question();
        secondQuestion.setQuiz(savedQuiz);
        secondQuestion.setQuestionText("Second question");
        secondQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        secondQuestion.setPosition(2);

        Question thirdQuestion = new Question();
        thirdQuestion.setQuiz(savedQuiz);
        thirdQuestion.setQuestionText("Third question");
        thirdQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        thirdQuestion.setPosition(3);

        questionRepository.saveAndFlush(thirdQuestion);
        questionRepository.saveAndFlush(firstQuestion);
        questionRepository.saveAndFlush(secondQuestion);

        var questions =
                questionRepository.findByQuizIdOrderByPositionAsc(
                        savedQuiz.getId()
                );

        assertThat(questions).hasSize(3);
        assertThat(questions.get(0).getPosition()).isEqualTo(1);
        assertThat(questions.get(1).getPosition()).isEqualTo(2);
        assertThat(questions.get(2).getPosition()).isEqualTo(3);
    }

    @Test
    void shouldOnlyFindQuestionsForSpecifiedQuiz() {
        User user = new User();
        user.setUsername("question-filter-test-user");
        user.setEmail("question-filter@example.com");
        user.setPasswordHash("temporary-test-hash");

        userRepository.save(user);

        Quiz firstQuiz = new Quiz();
        firstQuiz.setOwner(user);
        firstQuiz.setTitle("First Quiz");
        firstQuiz.setDescription("First quiz.");
        firstQuiz.setStatus(QuizStatus.DRAFT);

        Quiz secondQuiz = new Quiz();
        secondQuiz.setOwner(user);
        secondQuiz.setTitle("Second Quiz");
        secondQuiz.setDescription("Second quiz.");
        secondQuiz.setStatus(QuizStatus.DRAFT);

        quizRepository.saveAll(
                List.of(firstQuiz, secondQuiz)
        );

        Question firstQuizQuestion = new Question();
        firstQuizQuestion.setQuiz(firstQuiz);
        firstQuizQuestion.setQuestionText("First quiz question");
        firstQuizQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        firstQuizQuestion.setPosition(1);

        Question secondQuizQuestion = new Question();
        secondQuizQuestion.setQuiz(secondQuiz);
        secondQuizQuestion.setQuestionText("Second quiz question");
        secondQuizQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        secondQuizQuestion.setPosition(1);

        questionRepository.saveAll(
                List.of(firstQuizQuestion, secondQuizQuestion)
        );

        List<Question> questions =
                questionRepository.findByQuizIdOrderByPositionAsc(
                        firstQuiz.getId()
                );

        assertThat(questions).hasSize(1);

        assertThat(questions.get(0).getQuestionText())
                .isEqualTo("First quiz question");
    }

    @Test
    void shouldReturnEmptyListWhenQuizHasNoQuestions() {
        User user = new User();
        user.setUsername("empty-quiz-test-user");
        user.setEmail("empty-quiz@example.com");
        user.setPasswordHash("temporary-test-hash");

        userRepository.save(user);

        Quiz quiz = new Quiz();
        quiz.setOwner(user);
        quiz.setTitle("Empty Quiz");
        quiz.setDescription("Quiz with no questions");
        quiz.setStatus(QuizStatus.DRAFT);

        quizRepository.saveAndFlush(quiz);

        List<Question> questions =
                questionRepository.findByQuizIdOrderByPositionAsc(
                        quiz.getId()
                );

        assertThat(questions).isEmpty();
    }
}
