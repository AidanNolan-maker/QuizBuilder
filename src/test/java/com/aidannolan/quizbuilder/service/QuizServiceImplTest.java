package com.aidannolan.quizbuilder.service;

import com.aidannolan.quizbuilder.entity.Quiz;
import com.aidannolan.quizbuilder.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceImplTest {
    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void shouldCreateQuiz() {
        Quiz quiz = new Quiz();

        when(quizRepository.save(quiz))
                .thenReturn(quiz);

        Quiz result = quizService.createQuiz(quiz);

        assertThat(result).isSameAs(quiz);

        verify(quizRepository).save(quiz);
    }
}
