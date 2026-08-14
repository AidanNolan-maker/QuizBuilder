package com.aidannolan.quizbuilder.mapper;

import com.aidannolan.quizbuilder.dto.answer.AnswerRequestDTO;
import com.aidannolan.quizbuilder.dto.answer.AnswerResponseDTO;
import com.aidannolan.quizbuilder.entity.Answer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerMapperTest {
    private final AnswerMapper answerMapper = new AnswerMapper();

    @Test
    void shouldMapRequestToEntity() {
        AnswerRequestDTO dto = new AnswerRequestDTO(
                "extends",
                true,
                1
        );

        Answer answer = answerMapper.toEntity(dto);

        assertThat(answer.getId()).isNull();
        assertThat(answer.getAnswerText()).isEqualTo("extends");
        assertThat(answer.isCorrect()).isTrue();
        assertThat(answer.getPosition()).isEqualTo(1);
        assertThat(answer.getQuestion()).isNull();
    }

    @Test
    void shouldMapEntityToResponseDTO() {
        Answer answer = new Answer();

        answer.setId(1L);
        answer.setAnswerText("extends");
        answer.setCorrect(true);
        answer.setPosition(1);

        AnswerResponseDTO result =
                answerMapper.toResponseDTO(answer);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.answerText()).isEqualTo("extends");
        assertThat(result.correct()).isTrue();
        assertThat(result.position()).isEqualTo(1);
    }
}
