package lk.helphub.api.application.services;

import lk.helphub.api.domain.entity.RefreshToken;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${application.security.jwt.refresh-token-expiration-days:7}")
    private long refreshTokenExpirationDays;

    /**
     * Creates and persists a new refresh token for the given user.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validates a refresh token string. Throws if not found, revoked, or expired.
     */
    public RefreshToken validateRefreshToken(String tokenString) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        return token;
    }

    /**
     * Revokes a single refresh token by its token string.
     */
    @Transactional
    public void revokeToken(String tokenString) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    /**
     * Revokes all active refresh tokens for the given user (logout from all devices).
     */
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
