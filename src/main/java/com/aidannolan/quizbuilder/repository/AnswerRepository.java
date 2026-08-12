package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionIdOrderByPositionAsc(Long questionId);

    long countByQuestionIdAndCorrectTrue(Long questionId);
}
