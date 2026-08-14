package com.aidannolan.quizbuilder.mapper;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.dto.answer.AnswerResponseDTO;
import com.aidannolan.quizbuilder.entity.Answer;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {
    public Answer toEntity(AnswerRequestDTO dto) {
        Answer answer = new Answer();

        answer.setAnswerText(dto.answerText());
        answer.setCorrect(dto.correct());
        answer.setPosition(dto.position());

        return answer;
    }

    public AnswerResponseDTO toResponseDTO(Answer answer) {
        return new AnswerResponseDTO(
                answer.getId(),
                answer.getAnswerText(),
                answer.isCorrect(),
                answer.getPosition(),
                answer.getCreatedAt(),
                answer.getUpdatedAt()
        );
    }
}
