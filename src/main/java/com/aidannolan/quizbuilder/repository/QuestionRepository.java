package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizIdOrderByPositionAsc(Long quizId);
}
