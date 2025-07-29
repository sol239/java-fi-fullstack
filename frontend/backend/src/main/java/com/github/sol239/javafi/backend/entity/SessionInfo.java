package com.github.sol239.javafi.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

/**
 * Represents session information including session ID, creation time, and last accessed time.
 * This class is used to track user sessions in the application.
 */
@Data
@AllArgsConstructor
@ToString
public class SessionInfo {

    /**
     * Unique identifier for the session.
     */
    private String sessionId;

    /**
     * The time when the session was created.
     */
    private Instant creationTime;

    /**
     * The last time the session was accessed.
     */
    private Instant lastAccessedTime;
}
