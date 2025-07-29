package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Provides methods to find users by username and check existence of usernames.
 */
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Finds a user by their username.
     * @param username the username of the user to find
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username exists.
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);
}