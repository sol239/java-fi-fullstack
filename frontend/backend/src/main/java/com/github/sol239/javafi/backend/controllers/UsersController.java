package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.github.sol239.javafi.backend.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserRepository userRepository;

    public UsersController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        System.out.println("Adding user: " + user.getUsername());
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


}
