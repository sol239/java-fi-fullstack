package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.entity.User;
import com.github.sol239.javafi.backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.*;
import org.springframework.stereotype.Service;

/**
 * Service class that implements UserDetailsService to load user details from the database.
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    /**
     * Repository for accessing user data.
     */
    private final UserRepository repo;

    /**
     * Constructor for DatabaseUserDetailsService.
     *
     * @param repo the repository for accessing user data
     */
    public DatabaseUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    /**
     * Loads user details by username.
     *
     * @param username the username of the user to load
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
        // Convert comma‑separated roles into GrantedAuthority list
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                authorities
        );
    }
}