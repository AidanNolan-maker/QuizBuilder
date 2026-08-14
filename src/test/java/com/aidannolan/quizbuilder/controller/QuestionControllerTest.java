package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.entity.QuestionType;
import com.aidannolan.quizbuilder.exception.QuizNotFoundException;
import com.aidannolan.quizbuilder.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
public class QuestionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    @Test
    void shouldCreateQuestion() throws Exception {
        QuestionResponseDTO response = new QuestionResponseDTO(
                10L,
                1L,
                "Which keyword is used to inherit from a class?",
                QuestionType.MULTIPLE_CHOICE_SINGLE,
                1,
                List.of(),
                null,
                null
        );

        when(questionService.createQuestion(
                eq(1L),
                any(QuestionRequestDTO.class)
        )).thenReturn(response);

        mockMvc.perform(
                    post("/api/quizzes/1/questions")
                            .contentType("application/json")
                            .content("""
                                    {
                                        "questionText":
                                            "Which keyword is used to inherit from a class?",
                                        "questionType":
                                            "MULTIPLE_CHOICE_SINGLE",
                                        "position": 1,
                                        "answers": [
                                            {
                                                "answerText": "extends",
                                                "correct": true,
                                                "position": 1
                                            },
                                            {
                                                "answerText": "implements",
                                                "correct": false,
                                                "position": 2
                                            }
                                         ]
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.quizId").value(1))
                .andExpect(jsonPath("$.questionText")
                        .value(
                                "Which keyword is used to inherit from a class?"
                        ))
                .andExpect(jsonPath("$.questionType")
                        .value("MULTIPLE_CHOICE_SINGLE"))
                .andExpect(jsonPath("$.position").value(1));

        verify(questionService).createQuestion(
                eq(1L),
                any(QuestionRequestDTO.class)
        );
    }

    @Test
    void shouldRejectQuestionWithMultipleCorrectAnswers() throws Exception {
        mockMvc.perform(
                    post("/api/quizzes/1/questions")
                            .contentType("application/json")
                            .content("""
                                    {
                                        "questionText":
                                            "Which keyword is used to inherit from a class?",
                                         "questionType":
                                            "MULTIPLE_CHOICE_SINGLE",
                                         "position": 1,
                                         "answers": [
                                            {
                                                "answerText": "extends",
                                                "correct": true,
                                                "position": 1
                                            },
                                            {
                                                "answerText": "implements",
                                                "correct": true,
                                                "position": 2
                                            }
                                         ]
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(questionService);
    }

    @Test
    void shouldReturnNotFoundWhenQuizDoesNotExist() throws Exception {
        when(questionService.createQuestion(
                eq(999L),
                any(QuestionRequestDTO.class)
        )).thenThrow(new QuizNotFoundException(999L));

        mockMvc.perform(
                        post("/api/quizzes/999/questions")
                            .contentType("application/json")
                            .content("""
                                    {
                                        "questionText":
                                            "Which keyword is used to inherit from a class?",
                                        "questionType":
                                            "MULTIPLE_CHOICE_SINGLE",
                                        "position": 1,
                                        "answers": [
                                            {
                                                "answerText": "extends",
                                                "correct": true,
                                                "position": 1
                                            }
                                        ]
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
}
