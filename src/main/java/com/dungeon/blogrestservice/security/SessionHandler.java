package com.dungeon.blogrestservice.security;

import com.dungeon.blogrestservice.models.Session;

import java.util.Optional;

public class SessionHandler {
    private long id;
    private String token;
    private Session session;
    private boolean isBloggerLoggedIn;
    private boolean isTokenMatching;

    public SessionHandler(long id, String token, Optional<Session> optionalSession) {
        this.id = id;
        this.token = token;

        if (optionalSession.isPresent()) {
            session = optionalSession.get();
            isBloggerLoggedIn = true;
        }
        else {
            session = null;
            isBloggerLoggedIn = false;
            return;
        }

        if (session.getToken().compareTo(token) == 0) {
            isTokenMatching = true;
        }
    }


    public Session getSession() {
        return session;
    }

    public String getToken() {
        return token;
    }

    public long getId() {
        return id;
    }

    public boolean isBloggerLoggedIn() {
        return isBloggerLoggedIn;
    }

    public boolean isTokenMatching() {
        return isTokenMatching;
    }
}
