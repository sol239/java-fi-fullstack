package com.github.sol239.javafi.backend.config;

import com.github.sol239.javafi.backend.entity.SessionInfo;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.http.HttpSessionEvent;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
@Component
public class SessionCounter implements HttpSessionListener {

    private static final AtomicInteger activeSessions = new AtomicInteger();

    // Store session metadata
    private static final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        activeSessions.incrementAndGet();

        SessionInfo info = new SessionInfo(
                session.getId(),
                Instant.ofEpochMilli(session.getCreationTime()),
                Instant.ofEpochMilli(session.getLastAccessedTime())
        );

        sessions.put(session.getId(), info);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        activeSessions.decrementAndGet();
        sessions.remove(session.getId());
    }

    public static int getActiveSessions() {
        return activeSessions.get();
    }

    public static Map<String, SessionInfo> getAllSessions() {
        return sessions;
    }

    public static SessionInfo getSessionById(String sessionId) {
        return sessions.get(sessionId);
    }
}
