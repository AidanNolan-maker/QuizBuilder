package com.aidannolan.quizbuilder.mapper;

import com.aidannolan.quizbuilder.dto.answer.AnswerResponseDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionRequestDTO;
import com.aidannolan.quizbuilder.dto.question.QuestionResponseDTO;
import com.aidannolan.quizbuilder.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionMapper {
    private final AnswerMapper answerMapper;

    public Question toEntity(QuestionRequestDTO dto) {
        Question question = new Question();

        question.setQuestionText(dto.questionText());
        question.setQuestionType(dto.questionType());
        question.setPosition(dto.position());

        question.setAnswers(
                dto.answers()
                        .stream()
                        .map(answerMapper::toEntity)
                        .toList()
        );

        return question;
    }

    public QuestionResponseDTO toResponseDTO(Question question) {
        List<AnswerResponseDTO> answers = question.getAnswers()
                .stream()
                .map(answerMapper::toResponseDTO)
                .toList();

        return new QuestionResponseDTO(
                question.getId(),
                question.getQuiz().getId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getPosition(),
                answers,
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

    public void updateEntity(
            Question question,
            QuestionRequestDTO dto
    ) {
        question.setQuestionText(dto.questionText());
        question.setQuestionType(dto.questionType());
        question.setPosition(dto.position());
    }
}
