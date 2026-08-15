package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.entity.QuizStatus;
import com.aidannolan.quizbuilder.exception.UserNotFoundException;
import com.aidannolan.quizbuilder.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizService quizService;

    @Test
    void shouldGetQuizzesByOwner() throws Exception {
        QuizResponseDTO quiz = new QuizResponseDTO(
                10L,
                1L,
                "Java Fundamentals",
                "A quiz about Java",
                QuizStatus.DRAFT,
                null,
                null
        );

        when(quizService.getQuizzesByOwnerId(1L))
                .thenReturn(List.of(quiz));

        mockMvc.perform(
                    get("/api/users/1/quizzes")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].ownerId").value(1))
                .andExpect(jsonPath("$[0].title")
                        .value("Java Fundamentals"))
                .andExpect(jsonPath("$[0].status")
                        .value("DRAFT"));
    }

    @Test
    void shouldReturnEmptyListWhenOwnerHasNoQuizzes() throws Exception {
        when(quizService.getQuizzesByOwnerId(1L))
                .thenReturn(List.of());

        mockMvc.perform(
                    get("/api/users/1/quizzes")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        when(quizService.getQuizzesByOwnerId(999L))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(
                    get("/api/users/999/quizzes")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("User Not Found"))
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.detail")
                        .value("User not found: 999"));
    }
}
