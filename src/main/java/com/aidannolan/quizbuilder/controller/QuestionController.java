package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public QuestionResponseDTO createQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionRequestDTO request
    ) {
        return questionService.createQuestion(quizId, request);
    }

    @GetMapping
    public List<QuestionResponseDTO> getQuestionsByQuizId(
            @PathVariable Long quizId
    ) {
        return questionService.getQuestionsByQuizId(quizId);
    }

    @GetMapping("/{questionId}")
    public QuestionResponseDTO getQuestionById(
            @PathVariable Long quizId,
            @PathVariable Long questionId
    ) {
        return questionService.getQuestionById(quizId, questionId);
    }

    @PutMapping("/{questionId}")
    public QuestionResponseDTO updateQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequestDTO request
    ) {
        return questionService.updateQuestion(
                quizId,
                questionId,
                request
        );
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId
    ) {
        questionService.deleteQuestion(quizId, questionId);
    }
}
