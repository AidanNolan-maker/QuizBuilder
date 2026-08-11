package com.aidannolan.quizbuilder.repository;

import com.aidannolan.quizbuilder.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername() {
        User user = new User();

        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("temporary-test-hash");

        User savedUser = userRepository.save(user);

        Optional<User> foundUser =
                userRepository.findByUsername("testuser");

        assertThat(savedUser.getId()).isNotNull();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");

        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateUpdatedAtWhenUserChanges() {
        User user = new User();

        user.setUsername("testuser");
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
