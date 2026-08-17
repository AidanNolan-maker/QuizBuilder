package com.aidannolan.quizbuilder.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

@Configuration
public class JwtConfig {
    private final String keystorePath;
    private final String keystoreAlias;
    private final String keystorePassword;

    public JwtConfig(
            @Value("${jwt.keystore.path}") String keystorePath,
            @Value("${jwt.keystore.alias}") String keystoreAlias,
            @Value("${jwt.keystore.password}") String keystorePassword
    ) {
        this.keystorePath = keystorePath;
        this.keystoreAlias = keystoreAlias;
        this.keystorePassword = keystorePassword;
    }

    @Bean
    public KeyPair jwtKeyPair() {
        try {
            String password = keystorePassword;

            KeyStore keyStore =
                    KeyStore.getInstance("PKCS12");

            try (InputStream inputStream =
                         Files.newInputStream(
                                 Path.of(keystorePath)
                         )) {
                keyStore.load(
                        inputStream,
                        password.toCharArray()
                );
            }

            KeyStore.PrivateKeyEntry entry =
                    (KeyStore.PrivateKeyEntry)
                            keyStore.getEntry(
                                    keystoreAlias,
                                    new KeyStore.PasswordProtection(
                                            password.toCharArray()
                                    )
                            );

            if (entry == null) {
                throw new IllegalStateException(
                        "No private key entry found for alias: "
                            + keystoreAlias
                );
            }

            PrivateKey privateKey =
                    entry.getPrivateKey();

            Certificate certificate =
                    entry.getCertificate();

            return new KeyPair(
                    certificate.getPublicKey(),
                    privateKey
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to load JWT key pair",
                    exception
            );
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        RSAKey rsaKey =
                new RSAKey.Builder(
                        (java.security.interfaces.RSAPublicKey)
                            jwtKeyPair.getPublic()
                )
                        .privateKey(jwtKeyPair.getPrivate())
                        .keyID(keystoreAlias)
                        .build();

        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(
                        new JWKSet(rsaKey)
                );

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeyPair) {
        return NimbusJwtDecoder
                .withPublicKey(
                        (java.security.interfaces.RSAPublicKey)
                            jwtKeyPair.getPublic()
                )
                .build();
    }
}
