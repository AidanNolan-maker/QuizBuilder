package com.aidannolan.quizbuilder.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {
    private final PasswordEncoder passwordEncoder =
            new SecurityConfig().passwordEncoder();

    @Test
    void shouldEncodePassword() {
        String rawPassword = "password123";

        String encodedPassword =
                passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword)
                .isNotEqualTo(rawPassword)
                .isNotBlank();
    }

    @Test
    void shouldMatchCorrectPassword() {
        String rawPassword = "password123";

        String encodedPassword =
                passwordEncoder.encode(rawPassword);

        assertThat(
                passwordEncoder.matches(
                        rawPassword,
                        encodedPassword
                )
        ).isTrue();
    }

    @Test
    void shouldRejectIncorrectPassword() {
        String rawPassword = "password123";
        String incorrectPassword = "wrongPassword";

        String encodedPassword =
                passwordEncoder.encode(rawPassword);

        assertThat(
                passwordEncoder.matches(
                        incorrectPassword,
                        encodedPassword
                )
        ).isFalse();
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        String rawPassword = "password123";

        String firstHash =
                passwordEncoder.encode(rawPassword);

        String secondHash =
                passwordEncoder.encode(rawPassword);

        assertThat(firstHash)
                .isNotEqualTo(secondHash);

        assertThat(
                passwordEncoder.matches(
                        rawPassword,
                        firstHash
                )
        ).isTrue();

        assertThat(
                passwordEncoder.matches(
                        rawPassword,
                        secondHash
                )
        ).isTrue();
    }
}
