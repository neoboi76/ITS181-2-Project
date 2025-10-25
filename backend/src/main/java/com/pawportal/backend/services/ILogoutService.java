package com.pawportal.backend.services;

public interface ILogoutService {
    void blacklistToken(String token);
    boolean isTokenBlacklisted(String token);
    void purgeExpiredTokens();
}
