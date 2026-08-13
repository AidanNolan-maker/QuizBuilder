package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizUpdateRequestDTO;
import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.entity.QuizStatus;
import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.exception.UserNotFoundException;
import com.aidannolan.quizbuilder.mapper.QuizMapper;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import com.aidannolan.quizbuilder.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private QuizMapper quizMapper;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void shouldCreateQuiz() {
        User user = new User();
        user.setId(1L);

        QuizRequestDTO request = new QuizRequestDTO(
                1L,
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

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(quizRepository.save(any(Quiz.class)))
                .thenReturn(savedQuiz);

        when(quizMapper.toResponseDTO(savedQuiz))
                .thenReturn(response);

        QuizResponseDTO result =
                quizService.createQuiz(request);

        assertThat(result).isEqualTo(response);

        verify(userRepository).findById(1L);
        verify(quizRepository).save(any(Quiz.class));
        verify(quizMapper).toResponseDTO(savedQuiz);
    }

    @Test
    void shouldThrowExceptionWhenQuizDoesNotExist() {
        Long quizId = 999L;

        when(quizRepository.findById(quizId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizService.getQuizById(quizId))
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verify(quizRepository).findById(quizId);
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

        when(quizRepository.findById(quizId))
                .thenReturn(Optional.of(existingQuiz));

        when(quizRepository.save(existingQuiz))
                .thenReturn(existingQuiz);

        when(quizMapper.toResponseDTO(existingQuiz))
                .thenReturn(response);

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

        verify(quizRepository).findById(quizId);
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

        when(quizRepository.findById(quizId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> quizService.updateQuiz(quizId, request)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verify(quizRepository).findById(quizId);
        verify(quizRepository, never()).save(any());
        verifyNoInteractions(quizMapper);
    }

    @Test
    void shouldDeleteQuiz() {
        Long quizId = 1L;

        Quiz quiz = new Quiz();
        quiz.setId(quizId);

        when(quizRepository.findById(quizId))
                .thenReturn(Optional.of(quiz));

        quizService.deleteQuiz(quizId);

        verify(quizRepository).findById(quizId);
        verify(quizRepository).delete(quiz);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentQuiz() {
        Long quizId = 999L;

        when(quizRepository.findById(quizId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> quizService.deleteQuiz(quizId)
        )
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found: 999");

        verify(quizRepository).findById(quizId);
        verify(quizRepository, never()).delete(any());
    }
}
