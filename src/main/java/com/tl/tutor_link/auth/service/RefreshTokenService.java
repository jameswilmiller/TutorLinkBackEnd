package com.tl.tutor_link.auth.service;

import com.tl.tutor_link.auth.model.RefreshToken;
import com.tl.tutor_link.common.exception.UnauthorizedException;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.auth.repository.RefreshTokenRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RefreshTokenService {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }
    @Transactional
    public RefreshToken createRefreshToken(User user){
        log.debug("Creating refresh token for user {}", user.getId());
        String token = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiration() / 1000)
        );
        return refreshTokenRepository.save(refreshToken);
    }
    @Transactional(readOnly = true)
    public RefreshToken validateStoredRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token validation failed - token not found");
                    return new UnauthorizedException("Invalid refresh token");
                });

        if (refreshToken.isRevoked()) {
            log.warn("Attempt to use revoked refresh token for user {}", refreshToken.getUser().getId());
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Attempt to use expired refresh token for user {}", refreshToken.getUser().getId());
            throw new UnauthorizedException("Refresh token has expired");
        }
        return refreshToken;
    }
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }
    @Transactional
    public void revokeAllUserTokens(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser_IdAndRevokedFalse(user.getId());
        log.info("Revoking {} refresh tokens for user {}", tokens.size(), user.getId());
        for (RefreshToken token : tokens) {
            token.setRevoked(true);
        }

        refreshTokenRepository.saveAll(tokens);
    }


}
