package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.Answer;
import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.QuestionType;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.entity.QuizStatus;
import com.aidannolan.quizbuilder.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AnswerRepositoryTest {
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAnswerWithQuestion() {
        User user = new User();
        user.setUsername("answercreator");
        user.setEmail("answercreator@example.com");
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
        question.setQuestionText(
                "Which keyword is used to inherit from a class?"
        );
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question.setPosition(1);

        Question savedQuestion = questionRepository.saveAndFlush(question);

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAnswerText("extends");
        answer.setCorrect(true);
        answer.setPosition(1);

        Answer savedAnswer = answerRepository.saveAndFlush(answer);

        assertThat(savedAnswer.getId()).isNotNull();
        assertThat(savedAnswer.getQuestion().getId())
                .isEqualTo(savedQuestion.getId());
        assertThat(savedAnswer.getAnswerText())
                .isEqualTo("extends");
        assertThat(savedAnswer.isCorrect()).isTrue();
        assertThat(savedAnswer.getPosition()).isEqualTo(1);
        assertThat(savedAnswer.getCreatedAt()).isNotNull();
        assertThat(savedAnswer.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindAnswersInPositionOrder() {
        User user = new User();
        user.setUsername("answercreator");
        user.setEmail("answercreator@example.com");
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
        question.setQuestionText(
                "Which keyword is used to inherit from a class?"
        );
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question.setPosition(1);

        Question savedQuestion = questionRepository.saveAndFlush(question);

        Answer first = new Answer();
        first.setQuestion(savedQuestion);
        first.setAnswerText("extends");
        first.setCorrect(true);
        first.setPosition(1);

        Answer second = new Answer();
        second.setQuestion(savedQuestion);
        second.setAnswerText("implements");
        second.setCorrect(false);
        second.setPosition(2);

        Answer third = new Answer();
        third.setQuestion(savedQuestion);
        third.setAnswerText("inherits");
        third.setCorrect(false);
        third.setPosition(3);

        answerRepository.saveAndFlush(third);
        answerRepository.saveAndFlush(first);
        answerRepository.saveAndFlush(second);

        var answers =
                answerRepository.findByQuestionIdOrderByPositionAsc(
                        savedQuestion.getId()
                );

        assertThat(answers).hasSize(3);
        assertThat(answers.get(0).getPosition()).isEqualTo(1);
        assertThat(answers.get(1).getPosition()).isEqualTo(2);
        assertThat(answers.get(2).getPosition()).isEqualTo(3);
    }

    @Test
    void shouldCountCorrectAnswers() {
        User user = new User();
        user.setUsername("answercreator");
        user.setEmail("answercreator@example.com");
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
        question.setQuestionText(
                "Which keyword is used to inherit from a class?"
        );
        question.setQuestionType(QuestionType.MULTIPLE_CHOICE_SINGLE);
        question.setPosition(1);

        Question savedQuestion = questionRepository.saveAndFlush(question);

        Answer correctAnswer = new Answer();
        correctAnswer.setQuestion(savedQuestion);
        correctAnswer.setAnswerText("extends");
        correctAnswer.setCorrect(true);
        correctAnswer.setPosition(1);

        Answer incorrectAnswer = new Answer();
        incorrectAnswer.setQuestion(savedQuestion);
        incorrectAnswer.setAnswerText("inherits");
        incorrectAnswer.setCorrect(false);
        incorrectAnswer.setPosition(2);

        answerRepository.saveAndFlush(correctAnswer);
        answerRepository.saveAndFlush(incorrectAnswer);

        long correctCount =
                answerRepository.countByQuestionIdAndCorrectTrue(
                        savedQuestion.getId()
                );

        assertThat(correctCount).isEqualTo(1);
    }
}
