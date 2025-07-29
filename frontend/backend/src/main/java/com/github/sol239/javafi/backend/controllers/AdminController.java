package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.config.SessionCounter;
import com.github.sol239.javafi.backend.entity.ServerStatus;
import com.github.sol239.javafi.backend.entity.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.lang.management.ManagementFactory;
import java.util.Collection;

/**
 * AdminController provides endpoints for administrative tasks such as checking server status and session management.
 * It is secured to allow access only to users with the ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * Logger for AdminController to log messages related to administrative operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * Returns the current status of the server, including memory usage, uptime, active thread count, and total memory.
     * @return ServerStatus object containing server metrics.
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ServerStatus getStatus() {
        logger.info("GET:api/admin/status AdminController.getStatus() called");
        ServerStatus status = new ServerStatus(
                Runtime.getRuntime().freeMemory(),
                ManagementFactory.getRuntimeMXBean().getUptime(),
                Thread.activeCount(),
                Runtime.getRuntime().totalMemory(),
                Runtime.getRuntime().maxMemory()
        );
        System.out.println("Server Status: " + status);

        return status;
    }

    /**
     * Returns a collection of all active sessions on the server.
     * This endpoint is deprecated and should not be used.
     * Some other session management solution should be used.
     * @return Collection of SessionInfo objects representing active sessions.
     */
    @Deprecated
    @GetMapping("/sessions")
    public Collection<SessionInfo> listSessions() {
        return SessionCounter.getAllSessions().values();
    }

    /**
     * Returns information about a specific session by its ID.
     * @param id the ID of the session to retrieve
     * @return SessionInfo object containing session details
     */
    @Deprecated
    @GetMapping("/sessions/{id}")
    public SessionInfo getSession(@PathVariable String id) {
        SessionInfo info = SessionCounter.getSessionById(id);
        if (info == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
        }
        return info;
    }

    /**
     * Returns the count of active sessions on the server.
     * @return the number of active sessions
     */
    @Deprecated
    @GetMapping("/sessions/count")
    public int getSessionCount() {
        return SessionCounter.getActiveSessions();
    }



}
