package com.github.sol239.javafi.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/logout")
public class LogoutController {
    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

    @PostMapping
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("POST:api/logout LogoutController.logout() called");
        // Invalidate session
        request.getSession().invalidate();

        // Expire the session cookie
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }

}
