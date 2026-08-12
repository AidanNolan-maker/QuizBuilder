package com.aidannolan.quizbuilder.controller;

import com.aidannolan.quizbuilder.dto.quiz.QuizResponseDTO;
import com.aidannolan.quizbuilder.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final QuizService quizService;

    @GetMapping("/{userId}/quizzes")
    public List<QuizResponseDTO> getQuizzesByOwner(
            @PathVariable Long userId
    ) {
        return quizService.getQuizzesByOwnerId(userId);
    }
}
