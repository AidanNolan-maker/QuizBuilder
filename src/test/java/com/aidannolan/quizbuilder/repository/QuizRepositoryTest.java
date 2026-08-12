package com.aidannolan.quizbuilder.repository;

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
}
