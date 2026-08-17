package com.aidannolan.quizbuilder.service.impl;

import com.aidannolan.quizbuilder.service.JwtService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceImplTest {
    private JwtService jwtService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
       try {
           KeyPairGenerator generator =
                   KeyPairGenerator.getInstance("RSA");

           generator.initialize(2048);

           KeyPair keyPair =
                   generator.generateKeyPair();

           RSAKey rsaKey =
                   new RSAKey.Builder(
                           (RSAPublicKey) keyPair.getPublic()
                   )
                           .privateKey(keyPair.getPrivate())
                           .build();

           JWKSource<SecurityContext> jwkSource =
                   new ImmutableJWKSet<>(
                           new JWKSet(rsaKey)
                   );

           JwtEncoder jwtEncoder =
                   new NimbusJwtEncoder(jwkSource);

           jwtDecoder =
                   NimbusJwtDecoder
                           .withPublicKey(
                                   (RSAPublicKey)
                                        keyPair.getPublic()
                           )
                           .build();

           jwtService =
                   new JwtServiceImpl(jwtEncoder);
       } catch (Exception exception) {
           throw new IllegalStateException(
                   "Failed to create test JWT configuration",
                   exception
           );
       }
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
