package com.github.sol239.javafi.backend.auth;

import com.github.sol239.javafi.backend.entity.User;
import com.github.sol239.javafi.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitialization {

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