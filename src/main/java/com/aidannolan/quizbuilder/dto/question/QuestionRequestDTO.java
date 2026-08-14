package com.aidannolan.quizbuilder.dto.question;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.entity.QuestionType;
import com.aidannolan.quizbuilder.validation.ValidQuestionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@ValidQuestionRequest
public record QuestionRequestDTO(
        @NotBlank
        @Size(max = 1000)
        String questionText,

        @NotNull
        QuestionType questionType,

        @NotNull
        Integer position,

        @NotEmpty
        @Valid
        List<AnswerRequestDTO> answers
) {
}
