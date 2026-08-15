package com.aidannolan.quizbuilder.dto;

public record UserResponseDTO(
        Long id,
        String username,
        String email
) {
}
