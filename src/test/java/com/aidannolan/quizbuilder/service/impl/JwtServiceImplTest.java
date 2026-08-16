package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.config.JwtConfig;
import com.aidannolan.quizbuilder.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceImplTest {
    private JwtService jwtService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();

        var keyPair = jwtConfig.jwtKeyPair();

        JwtEncoder jwtEncoder =
                jwtConfig.jwtEncoder(keyPair);

        jwtDecoder =
                jwtConfig.jwtDecoder(keyPair);

        jwtService =
                new JwtServiceImpl(jwtEncoder);
    }

    @Test
    void shouldGenerateToken() {
        String token =
                jwtService.generateToken("aidan");

        assertThat(token)
                .isNotBlank();
    }

    @Test
    void shouldIncludeUsernameAsSubject() {
        String token =
                jwtService.generateToken("aidan");

        Jwt decodedToken =
                jwtDecoder.decode(token);

        assertThat(decodedToken.getSubject())
            .isEqualTo("aidan");
    }

    @Test
    void shouldIncludeIssuedAtClaim() {
        String token =
                jwtService.generateToken("aidan");

        Jwt decodedToken =
                jwtDecoder.decode(token);

        assertThat(decodedToken.getIssuedAt())
                .isNotNull();
    }

    @Test
    void shouldIncludeExpirationClaim() {
        String token =
                jwtService.generateToken("aidan");

        Jwt decodedToken =
                jwtDecoder.decode(token);

        assertThat(decodedToken.getExpiresAt())
                .isNotNull();
    }

    @Test
    void shouldExpireOneHourAfterIssuance() {
        String token =
                jwtService.generateToken("aidan");

        Jwt decodedToken =
                jwtDecoder.decode(token);

        assertThat(decodedToken.getExpiresAt())
                .isNotNull();

        assertThat(decodedToken.getIssuedAt())
                .isNotNull();

        long lifetime =
                decodedToken.getExpiresAt().getEpochSecond()
                    - decodedToken.getIssuedAt().getEpochSecond();

        assertThat(lifetime)
                .isEqualTo(3600);
    }
}
