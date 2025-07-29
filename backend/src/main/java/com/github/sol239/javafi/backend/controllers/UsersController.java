package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.sol239.javafi.backend.repositories.UserRepository;

/**
 * Controller for managing user-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UsersController {

    /**
     * Logger for logging user-related operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    /**
     * Password encoder for encoding user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Constructor for UsersController.
     *
     * @param userRepository the repository for accessing user data
     * @param passwordEncoder the password encoder for encoding user passwords
     */
    public UsersController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint to get the roles of a user by username.
     *
     * @param username the username of the user
     * @return a list of roles associated with the user
     */
    @GetMapping("/{username}/roles")
    public List<String> getUserRoles(@PathVariable String username) {
        logger.info("GET:api/users/roles UsersController.getUserRoles() called for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        List<String> roles = new ArrayList<>(user.getRoles());
        return roles;
    }

    /**
     * Endpoint to get all users.
     *
     * @return a list of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        logger.info("GET:api/users UsersController.getAllUsers() called");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "anonymous";

        System.out.println(username);

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        return userRepository.findAll();
    }

    /**
     * Endpoint to delete a user by ID.
     * @param id the ID of the user to delete
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        System.out.println("Deleting user with ID: " + id);
        userRepository.deleteById(id);
        logger.info("DELETE:api/users/delete/{} UsersController.deleteUser() called for user ID: {}", id, id);
    }

    /**
     * Endpoint to add a new user.
     *
     * @param user the user to add
     * @return the added user
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public User addUser(@RequestBody User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("POST:api/users/add UsersController.addUser() - User with username {} already exists.", user.getUsername());
            throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists.");
        }

        System.out.println("Adding user: " + user.getUsername());
        logger.info("POST:api/users/add UsersController.addUser() called for user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Endpoint to enable a user by ID.
     * @param id the ID of the user to enable
     * @return the updated user
     */
    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public User enableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(true);
        logger.info("POST:api/users/{}/enable UsersController.enableUser() called for user ID: {}", id, id);
        return userRepository.save(user);
    }

    /**
     * Endpoint to disable a user by ID.
     * @param id the ID of the user to disable
     * @return the updated user
     */
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public User disableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(false);
        logger.info("POST:api/users/{}/disable UsersController.disableUser() called for user ID: {}", id, id);
        return userRepository.save(user);
    }

    /**
     * Endpoint to reset a user's password.
     * @param request the request containing user ID and new password
     * @return the updated user with the new password
     */
    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public User resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        logger.info("POST:api/users/reset UsersController.resetPassword() called for user ID: {}", request.getUserId());
        return userRepository.save(user);
    }

    /**
     * Request object for resetting a user's password.
     */
    @Setter
    @Getter
    public static class ResetPasswordRequest {
        /**
         * The ID of the user whose password is to be reset.
         */
        private Long userId;

        /**
         * The new password for the user.
         */
        private String password;
    }


}
