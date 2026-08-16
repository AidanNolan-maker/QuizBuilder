package com.aidannolan.quizbuilder.service;

public interface JwtService {
    String generateToken(String username);
}
