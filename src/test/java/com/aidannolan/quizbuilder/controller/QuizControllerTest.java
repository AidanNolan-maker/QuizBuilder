package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.entity.QuizStatus;
import com.aidannolan.quizbuilder.service.QuizService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}
