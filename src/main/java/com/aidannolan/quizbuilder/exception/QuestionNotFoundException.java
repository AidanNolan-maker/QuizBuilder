package com.aidannolan.quizbuilder.exception;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(Long questionId) {
        super("Question not found: " + questionId);
    }
}
