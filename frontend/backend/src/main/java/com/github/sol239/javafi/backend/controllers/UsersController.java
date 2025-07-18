package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.github.sol239.javafi.backend.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UsersController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        System.out.println("Getting all users");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "anonymous";


        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        System.out.println("Role uživatele "+ username + ":"  + roles);

        return userRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        System.out.println("Deleting user with ID: " + id);
        userRepository.deleteById(id);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public User addUser(@RequestBody User user) {

        // Check if the user already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists.");
        }

        System.out.println("Adding user: " + user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public User enableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public User disableUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(false);
        return userRepository.save(user);
    }

    // Přidáno: endpoint pro reset hesla
    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public User resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    // Pomocná třída pro data z požadavku
    public static class ResetPasswordRequest {
        private Long userId;
        private String password;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }


}
