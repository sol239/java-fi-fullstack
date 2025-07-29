package com.github.sol239.javafi.backend.config;

import com.github.sol239.javafi.backend.entity.User;
import com.github.sol239.javafi.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Initializes the user database with default users if the repository is empty.
 */
@Configuration
public class UserInitialization {

    /**
     * Initializes the user repository with default users.
     * @param repo UserRepository to be initialized
     * @param encoder PasswordEncoder to encode user passwords
     * @return CommandLineRunner that runs on application startup
     */
    @Bean
    CommandLineRunner userInit(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new User(
                        null,
                        "admin",
                        encoder.encode("password"),
                        true,
                        Set.of("ADMIN", "USER")
                ));
                repo.save(new User(
                        null,
                        "user1",
                        encoder.encode("password1"),
                        true,
                        Set.of("USER")
                ));
                repo.save(new User(
                        null,
                        "user2",
                        encoder.encode("password2"),
                        true,
                        Set.of("USER")
                ));
            }
        };
    }
}