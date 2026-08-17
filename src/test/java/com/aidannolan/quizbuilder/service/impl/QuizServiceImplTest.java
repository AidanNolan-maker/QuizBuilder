package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizUpdateRequestDTO;
import com.aidannolan.quizbuilder.entity.Question;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.entity.QuizStatus;
import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.exception.QuestionNotFoundException;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.exception.UserNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuizMapper;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import com.aidannolan.quizbuilder.repository.UserRepository;
import com.aidannolan.quizbuilder.repository.QuestionRepository;
import com.aidannolan.quizbuilder.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceImplTest {
    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizMapper quizMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private QuizServiceImpl quizService;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Test
    void shouldCreateQuiz() {
        User user = new User();
        user.setId(1L);

        QuizRequestDTO request = new QuizRequestDTO(
                "Java Fundamentals",
                "A quiz about Java.",
                QuizStatus.DRAFT
        );

        Quiz quiz = new Quiz();
        Quiz savedQuiz = new Quiz();

        QuizResponseDTO response  = new QuizResponseDTO(
                1L,
                1L,
                "Java Fundamentals",
                "A quiz about Java.",
                QuizStatus.DRAFT,
                null,
                null
        );

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(user));

        when(quizRepository.save(any(Quiz.class)))
                .thenReturn(savedQuiz);

        when(quizMapper.toResponseDTO(savedQuiz))
                .thenReturn(response);

        QuizResponseDTO result =
                quizService.createQuiz(request);

        assertThat(result).isEqualTo(response);

        verify(userRepository).findByUsername("aidan");
        verify(quizRepository).save(any(Quiz.class));
        verify(quizMapper).toResponseDTO(savedQuiz);

        ArgumentCaptor<Quiz> quizCaptor =
                ArgumentCaptor.forClass(Quiz.class);

        verify(quizRepository)
                .save(quizCaptor.capture());

        assertThat(quizCaptor.getValue().getOwner())
                .isSameAs(user);
    }

    @Test
    void shouldGetQuizByIdWhenAuthenticatedUserOwnsQuiz() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setOwner(owner);
        quiz.setTitle("Java Fundamentals");
        quiz.setDescription("A quiz about Java.");
        quiz.setStatus(QuizStatus.DRAFT);

        QuizResponseDTO response = new QuizResponseDTO(
                quizId,
                owner.getId(),
                "Java Fundamentals",
                "A quiz about Java.",
                QuizStatus.DRAFT,
                null,
                null
        );

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.of(quiz));

        when(quizMapper.toResponseDTO(quiz))
                .thenReturn(response);

        QuizResponseDTO result =
                quizService.getQuizById(quizId);

        assertThat(result)
                .isEqualTo(response);

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(quizId, owner.getId());

        verify(quizMapper)
                .toResponseDTO(quiz);
    }

    @Test
    void shouldThrowExceptionWhenQuizDoesNotExist() {
        Long quizId = 999L;

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        User owner = new User();
        owner.setId(10L);

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizService.getQuizById(quizId))
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(quizId, owner.getId());
    }

    @Test
    void shouldGetQuizzesByOwnerId() {
        Long ownerId = 1L;

        User user = new User();
        user.setId(ownerId);

        Quiz quiz = new Quiz();
        quiz.setId(10L);
        quiz.setOwner(user);
        quiz.setTitle("Java Fundamentals");
        quiz.setDescription("A quiz about Java.");
        quiz.setStatus(QuizStatus.DRAFT);

        QuizResponseDTO response = new QuizResponseDTO(
                10L,
                ownerId,
                "Java Fundamentals",
                "A quiz about Java.",
                QuizStatus.DRAFT,
                null,
                null
        );

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(user));

        when(quizRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(quiz));

        when(quizMapper.toResponseDTO(quiz))
                .thenReturn(response);

        List<QuizResponseDTO> result =
                quizService.getQuizzesByOwnerId(ownerId);

        assertThat(result)
                .hasSize(1)
                .containsExactly(response);

        verify(userRepository).findById(ownerId);
        verify(quizRepository).findByOwnerId(ownerId);
        verify(quizMapper).toResponseDTO(quiz);
    }

    @Test
    void shouldReturnEmptyListWhenOwnerHasNoQuizzes() {
        Long ownerId = 1L;

        User user = new User();
        user.setId(ownerId);

        when(userRepository.findById(ownerId))
            .thenReturn(Optional.of(user));

        when(quizRepository.findByOwnerId(ownerId))
                .thenReturn(List.of());

        List<QuizResponseDTO> result =
                quizService.getQuizzesByOwnerId(ownerId);

        assertThat(result).isEmpty();

        verify(userRepository).findById(ownerId);
        verify(quizRepository).findByOwnerId(ownerId);
        verifyNoInteractions(quizMapper);
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotExist() {
        Long ownerId = 999L;

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> quizService.getQuizzesByOwnerId(ownerId)
        )
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found: 999");

        verify(userRepository).findById(ownerId);
        verifyNoInteractions(quizRepository);
        verifyNoInteractions(quizMapper);
    }

    @Test
    void shouldUpdateQuiz() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        Quiz existingQuiz = new Quiz();
        existingQuiz.setId(quizId);
        existingQuiz.setOwner(owner);
        existingQuiz.setTitle("Java Fundamentals");
        existingQuiz.setDescription("Original description");
        existingQuiz.setStatus(QuizStatus.DRAFT);

        QuizUpdateRequestDTO request = new QuizUpdateRequestDTO(
                "Advanced Java Fundamentals",
                "Updated description",
                QuizStatus.PUBLISHED
        );

        QuizResponseDTO response = new QuizResponseDTO(
                quizId,
                10L,
                "Advanced Java Fundamentals",
                "Updated description",
                QuizStatus.PUBLISHED,
                null,
                null
        );

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.of(existingQuiz));

        when(quizRepository.save(existingQuiz))
                .thenReturn(existingQuiz);

        when(quizMapper.toResponseDTO(existingQuiz))
                .thenReturn(response);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        QuizResponseDTO result =
                    quizService.updateQuiz(quizId, request);

        assertThat(result).isEqualTo(response);

        assertThat(existingQuiz.getTitle())
                .isEqualTo("Advanced Java Fundamentals");

        assertThat(existingQuiz.getDescription())
                .isEqualTo("Updated description");

        assertThat(existingQuiz.getStatus())
                .isEqualTo(QuizStatus.PUBLISHED);

        assertThat(existingQuiz.getOwner())
                .isSameAs(owner);

        verify(authenticationService)
            .getCurrentUsername();
        verify(userRepository)
                .findByUsername("aidan");
        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );
        verify(quizRepository).save(existingQuiz);
        verify(quizMapper).toResponseDTO(existingQuiz);
    }

    @Test
    void shouldThrowExceptionWhenQuizToUpdateDoesNotExist() {
        Long quizId = 999L;

        QuizUpdateRequestDTO request = new QuizUpdateRequestDTO(
                "Updated Quiz",
                "Updated description",
                QuizStatus.PUBLISHED
        );

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        User owner = new User();
        owner.setId(10L);

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.empty());

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        assertThatThrownBy(
                () -> quizService.updateQuiz(quizId, request)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );

        verify(quizRepository, never())
                .save(any());

        verifyNoInteractions(quizMapper);
    }

    @Test
    void shouldDeleteQuiz() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setOwner(owner);

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.of(quiz));

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        quizService.deleteQuiz(quizId);

        verify(authenticationService)
                .getCurrentUsername();
        verify(userRepository)
                .findByUsername("aidan");
        verify(quizRepository).findByIdAndOwnerId(
                quizId,
                owner.getId()
        );
        verify(quizRepository).delete(quiz);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentQuiz() {
        Long quizId = 999L;

        User owner = new User();
        owner.setId(10L);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));


        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> quizService.deleteQuiz(quizId)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );
        verify(quizRepository, never()).delete(any());
    }

    @Test
    void shouldDeleteQuestion() {
        User owner = new User();
        owner.setId(10L);

        Long quizId = 1L;
        Long questionId = 10L;

        Quiz quiz = new Quiz();
        quiz.setId(quizId);

        Question question = new Question();
        question.setId(questionId);
        question.setQuiz(quiz);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.of(quiz));

        when(questionRepository.findByIdAndQuizId(
                questionId,
                quizId
        )).thenReturn(Optional.of(question));

        questionService.deleteQuestion(quizId, questionId);

        verify(questionRepository)
                .findByIdAndQuizId(questionId, quizId);

        verify(questionRepository).delete(question);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentQuestion() {
        User owner = new  User();
        owner.setId(10L);

        Long quizId = 1L;
        Long questionId = 999L;

        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setOwner(owner);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
            .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.of(quiz));

        when(questionRepository.findByIdAndQuizId(
                questionId,
                quizId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> questionService.deleteQuestion(
                        quizId,
                        questionId
                )
        )
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessage("Question not found: " + questionId);

        verify(authenticationService)
            .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );

        verify(questionRepository)
                .findByIdAndQuizId(questionId, quizId);

        verify(questionRepository, never())
                .delete(any(Question.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingQuestionFromAnotherQuiz() {
        User owner = new User();
        owner.setId(10L);

        Long requestedQuizId = 1L;
        Long questionId = 10L;

        Quiz  requestedQuiz = new Quiz();
        requestedQuiz.setId(requestedQuizId);
        requestedQuiz.setOwner(owner);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                requestedQuizId,
                owner.getId()
        )).thenReturn(Optional.of(requestedQuiz));

        when(questionRepository.findByIdAndQuizId(
                questionId,
                requestedQuizId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> questionService.deleteQuestion(
                        requestedQuizId,
                        questionId
                )
        )
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessage("Question not found: " + questionId);

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        requestedQuizId,
                        owner.getId()
                );

        verify(questionRepository)
                .findByIdAndQuizId(questionId, requestedQuizId);

        verify(questionRepository, never())
                .delete(any(Question.class));
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatedUserDoesNotExist() {
        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.empty());

        QuizRequestDTO request = new QuizRequestDTO(
                "Java Fundamentals",
                "A quiz about Java.",
                QuizStatus.DRAFT
        );

        assertThatThrownBy(
                () -> quizService.createQuiz(request)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Authenticated user not found: aidan");

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verifyNoInteractions(quizRepository);
        verifyNoInteractions(quizMapper);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatedUserDoesNotOwnQuiz() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> quizService.getQuizById(quizId)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: " + quizId);

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );

        verifyNoInteractions(quizMapper);
    }

    @Test
    void shouldUpdateQuizWhenAuthenticatedUserOwnsQuiz() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        Quiz existingQuiz = new Quiz();
        existingQuiz.setId(quizId);
        existingQuiz.setOwner(owner);
        existingQuiz.setTitle("Java Fundamentals");
        existingQuiz.setDescription("Original description");
        existingQuiz.setStatus(QuizStatus.DRAFT);

        QuizUpdateRequestDTO request = new QuizUpdateRequestDTO(
                "Advanced Java Fundamentals",
                "Updated description",
                QuizStatus.PUBLISHED
        );

        QuizResponseDTO response = new QuizResponseDTO(
                quizId,
                owner.getId(),
                "Advanced Java Fundamentals",
                "Updated description",
                QuizStatus.PUBLISHED,
                null,
                null
        );

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.of(existingQuiz));

        when(quizRepository.save(existingQuiz))
                .thenReturn(existingQuiz);

        when(quizMapper.toResponseDTO(existingQuiz))
                .thenReturn(response);

        QuizResponseDTO result =
                quizService.updateQuiz(quizId, request);

        assertThat(result)
                .isEqualTo(response);

        assertThat(existingQuiz.getTitle())
                .isEqualTo("Advanced Java Fundamentals");

        assertThat(existingQuiz.getDescription())
                .isEqualTo("Updated description");

        assertThat(existingQuiz.getStatus())
                .isEqualTo(QuizStatus.PUBLISHED);

        assertThat(existingQuiz.getOwner())
                .isSameAs(owner);

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );

        verify(quizRepository)
                .save(existingQuiz);

        verify(quizMapper)
                .toResponseDTO(existingQuiz);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingQuizNotOwnedByAuthenticatedUser() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        QuizUpdateRequestDTO request = new QuizUpdateRequestDTO(
                "Updated Quiz",
                "Updated description",
                QuizStatus.PUBLISHED
        );

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> quizService.updateQuiz(quizId, request)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: " + quizId);

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );

        verify(quizRepository, never())
                .save(any());

        verifyNoInteractions(quizMapper);
    }

    @Test
    void shouldDeleteQuizWhenAuthenticatedUserOwnsQuiz() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setOwner(owner);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.of(quiz));

        quizService.deleteQuiz(quizId);

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );

        verify(quizRepository)
                .delete(quiz);
    }

    @Test
    void shouldThrowExceptionWhenDeletingQuizNotOwnedByAuthenticatedUser() {
        Long quizId = 1L;

        User owner = new User();
        owner.setId(10L);

        when(authenticationService.getCurrentUsername())
                .thenReturn("aidan");

        when(userRepository.findByUsername("aidan"))
                .thenReturn(Optional.of(owner));

        when(quizRepository.findByIdAndOwnerId(
                quizId,
                owner.getId()
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> quizService.deleteQuiz(quizId)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: " + quizId);

        verify(authenticationService)
                .getCurrentUsername();

        verify(userRepository)
                .findByUsername("aidan");

        verify(quizRepository)
                .findByIdAndOwnerId(
                        quizId,
                        owner.getId()
                );

        verify(quizRepository, never())
                .delete(any());
    }
}
