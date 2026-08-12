package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.quiz.QuizRequestDTO;
import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponseDTO createQuiz(
            @Valid @RequestBody QuizRequestDTO request
    ) {
        return quizService.createQuiz(request);
    }

    @GetMapping("/{id}")
    public QuizResponseDTO getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }

}
