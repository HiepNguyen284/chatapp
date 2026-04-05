package com.group4.chatapp.services;

import com.group4.chatapp.dtos.token.TokenObtainPairDto;
import com.group4.chatapp.dtos.token.TokenRefreshDto;
import com.group4.chatapp.dtos.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtsService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwts.access-token-lifetime}")
    private Duration accessTokenLifetime;

    @Value("${jwts.refresh-token-lifetime}")
    private Duration refreshTokenLifetime;

    private Jwt generateToken(Authentication authentication, Duration duration) {

        var issued = Instant.now();
        var expiration = issued.plus(duration);

        var claimsSet = JwtClaimsSet.builder()
            .subject(authentication.getName())
            .id(UUID.randomUUID().toString())
            .expiresAt(expiration)
            .issuedAt(issued)
            .build();

        var headers = JwsHeader.with(MacAlgorithm.HS256).build();
        var parameter = JwtEncoderParameters.from(headers, claimsSet);

        return jwtEncoder.encode(parameter);
    }

    public TokenObtainPairDto tokenObtainPair(UserDto dto) {

        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.username(), dto.password()
            )
        );

        var accessToken = generateToken(authentication, accessTokenLifetime);
        var refreshToken = generateToken(authentication, refreshTokenLifetime);

        refreshTokenService.storeRefreshToken(
            refreshToken.getId(),
            authentication.getName(),
            refreshTokenLifetime
        );

        return new TokenObtainPairDto(
            accessToken.getTokenValue(),
            refreshToken.getTokenValue()
        );
    }

    public TokenRefreshDto refreshToken(String refreshTokenValue) {

        var refreshToken = jwtDecoder.decode(refreshTokenValue);
        var jti = refreshToken.getId();

        if (!refreshTokenService.isValidRefreshToken(jti)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or revoked refresh token");
        }

        var username = refreshToken.getSubject();
        var authentication = new UsernamePasswordAuthenticationToken(username, null);

        var accessToken = generateToken(authentication, accessTokenLifetime);
        return new TokenRefreshDto(accessToken.getTokenValue());
    }

    public void revokeRefreshToken(String refreshTokenValue) {

        var refreshToken = jwtDecoder.decode(refreshTokenValue);
        var jti = refreshToken.getId();
        refreshTokenService.revokeRefreshToken(jti);
    }
}
