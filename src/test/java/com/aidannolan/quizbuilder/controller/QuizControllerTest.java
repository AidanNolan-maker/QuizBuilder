package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizUpdateRequestDTO;
import com.aidannolan.quizbuilder.entity.QuizStatus;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.service.QuizService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(QuizController.class)
@WithMockUser
public class QuizControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizService quizService;

    @Test
    void shouldCreateQuiz() throws Exception {
        QuizRequestDTO request = new QuizRequestDTO(
                1L,
                "Java Fundamentals",
                "A quiz covering basic Java concepts.",
                QuizStatus.DRAFT
        );

        QuizResponseDTO response = new QuizResponseDTO(
                1L,
                1L,
                "Java Fundamentals",
                "A quiz covering basic Java concepts.",
                QuizStatus.DRAFT,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(quizService.createQuiz(any(QuizRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/quizzes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Java Fundamentals"))
                .andExpect(jsonPath("$.status")
                        .value("DRAFT"));

        verify(quizService).createQuiz(any(QuizRequestDTO.class));
    }

    @Test
    void shouldRejectQuizWithBlankTitle() throws Exception {
        QuizRequestDTO request = new QuizRequestDTO(
                1L,
                "",
                "A quiz covering basic Java concepts.",
                QuizStatus.DRAFT
        );

        mockMvc.perform(
                    post("/api/quizzes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateQuiz() throws Exception {
        QuizUpdateRequestDTO request = new QuizUpdateRequestDTO(
                "Advanced Java Fundamentals",
                "An advanced Java quiz.",
                QuizStatus.PUBLISHED
        );

        QuizResponseDTO response = new QuizResponseDTO(
                1L,
                1L,
                "Advanced Java Fundamentals",
                "An advanced Java quiz.",
                QuizStatus.PUBLISHED,
                null,
                null
        );

        when(quizService.updateQuiz(
                eq(1L),
                any(QuizUpdateRequestDTO.class)
        )).thenReturn(response);

        mockMvc.perform(
                    put("/api/quizzes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "title": "Advanced Java Fundamentals",
                                        "description": "An advanced Java quiz.",
                                        "status": "PUBLISHED"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Advanced Java Fundamentals"))
                .andExpect(jsonPath("$.description")
                        .value("An advanced Java quiz."))
                .andExpect(jsonPath("$.status")
                        .value("PUBLISHED"));

        verify(quizService).updateQuiz(
                eq(1L),
                any(QuizUpdateRequestDTO.class)
        );
    }

    @Test
    void shouldRejectQuizWithBlankTitleWhenUpdating() throws Exception {
        mockMvc.perform(
                    put("/api/quizzes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "title": "",
                                        "description": "An updated quiz.",
                                        "status": "PUBLISHED"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(quizService);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentQuiz() throws Exception {
        when(quizService.updateQuiz(
                eq(999L),
                any(QuizUpdateRequestDTO.class)
        )).thenThrow(new QuizNotFoundException(999L));

        mockMvc.perform(
                    put("/api/quizzes/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "title": "Updated Quiz",
                                        "description": "Updated description.",
                                        "status": "PUBLISHED"
                                    }
                                    """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Quiz Not Found"))
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Quiz not found: 999"));
    }

    @Test
    void shouldDeleteQuiz() throws Exception {
        mockMvc.perform(
                    delete("/api/quizzes/1")
                            .with(csrf())
                )
                .andExpect(status().isNoContent());

        verify(quizService).deleteQuiz(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonexistentQuiz() throws Exception {
        doThrow(new QuizNotFoundException(999L))
                .when(quizService)
                .deleteQuiz(999L);

        mockMvc.perform(
                    delete("/api/quizzes/999")
                            .with(csrf())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Quiz Not Found"))
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Quiz not found: 999"));
    }
}
