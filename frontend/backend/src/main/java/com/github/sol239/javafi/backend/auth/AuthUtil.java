package com.github.sol239.javafi.backend.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class to handle authentication-related operations.
 * Currently, it provides a method to retrieve the username of the authenticated user.
 */
public class AuthUtil {

    /**
     * Retrieves the username of the currently authenticated user.
     * @return the username of the authenticated user, or "anonymous" if no user is authenticated.
     */
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : "anonymous";
    }
}
