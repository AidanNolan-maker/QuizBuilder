package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long>{
    List<Quiz> findByOwnerId(Long ownerId);

    Optional<Quiz> findByIdAndOwnerId(Long id, Long ownerId);
}
