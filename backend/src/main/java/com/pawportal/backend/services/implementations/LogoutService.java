package com.pawportal.backend.services.implementations;

import com.pawportal.backend.models.TokenBlackList;
import com.pawportal.backend.repositories.TokenRepository;
import com.pawportal.backend.services.interfaces.ILogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LogoutService implements ILogoutService {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void blacklistToken(String token) {
        if (token != null) {
            Date expiryDate = jwtTokenProvider.getExpirationDateFromToken(token);
            TokenBlackList blacklistedToken = new TokenBlackList(token, expiryDate.toInstant());
            tokenRepository.save(blacklistedToken);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenRepository.existsByToken(token);
    }

    @Override
    @Scheduled(fixedRate = 3600000)
    public void purgeExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}