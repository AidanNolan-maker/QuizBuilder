package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
