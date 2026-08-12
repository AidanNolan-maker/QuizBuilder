package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.User;
import com.aidannolan.quizbuilder.config.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername() {
        User user = new User();

        user.setUsername("repository-test-user");
        user.setEmail("test@example.com");
        user.setPasswordHash("temporary-test-hash");

        User savedUser = userRepository.save(user);

        Optional<User> foundUser =
                userRepository.findByUsername("repository-test-user");

        assertThat(savedUser.getId()).isNotNull();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("repository-test-user");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");

        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateUpdatedAtWhenUserChanges() {
        User user = new User();

        user.setUsername("updated-at-test-user");
        user.setEmail("test@example.com");
        user.setPasswordHash("temporary-test-hash");

        User savedUser = userRepository.save(user);

        var originalUpdatedAt = savedUser.getUpdatedAt();

        savedUser.setEmail("updated@example.com");

        User updatedUser = userRepository.saveAndFlush(savedUser);

        assertThat(updatedUser.getUpdatedAt())
                .isAfterOrEqualTo(originalUpdatedAt);

        assertThat(updatedUser.getEmail())
                .isEqualTo("updated@example.com");
    }
}
