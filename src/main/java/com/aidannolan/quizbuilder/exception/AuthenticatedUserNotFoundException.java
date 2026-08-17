package com.aidannolan.quizbuilder.exception;

public class AuthenticatedUserNotFoundException extends RuntimeException {
    public AuthenticatedUserNotFoundException(String username) {
        super("Authenticated user not found: " + username);
    }
}
