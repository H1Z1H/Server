package com.brick.brick_server.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.brick.brick_server.common.jwt.dto.TokenResponse;
import com.brick.brick_server.domain.user.domain.vo.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;

public class Jwt {
    private final String issuer;
    private final Long tokenExpire;
    private final Long refreshTokenExpire;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;

    public Jwt(String clientSecret, String issuer, Long tokenExpire, Long refreshTokenExpire) {
        this.issuer = issuer;
        this.tokenExpire = tokenExpire;
        this.refreshTokenExpire = refreshTokenExpire;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer).build();
    }

    public String generateAccessToken(Claims claims) {
        return sign(claims, tokenExpire);
    }

    public String generateRefreshToken(Claims claims) {
        return sign(claims, refreshTokenExpire);
    }

    public TokenResponse generateAllToken(Claims claims) {
        return new TokenResponse(generateAccessToken(claims), generateRefreshToken(claims));
    }

    private String sign(Claims claims, Long expireTime) {
        Date now = new Date();
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + expireTime * 1000L))
                .withClaim("userId", claims.userId)
                .withArrayClaim("roles", Arrays.stream(claims.roles)  // roles를 문자열 배열로 변환
                        .map(Role::getRole)  // Role 객체에서 이름(String)으로 변환
                        .toArray(String[]::new))  // String[] 배열로 변환
                .sign(algorithm);
    }

    public Claims verify(String token) {
        return new Claims(jwtVerifier.verify(token));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Claims {
        Long userId;
        Role[] roles;
        Date iat;
        Date exp;

        Claims(DecodedJWT decodedJwt) {
            Claim memberId = decodedJwt.getClaim("userId");
            if (!memberId.isNull()) {
                this.userId = memberId.asLong();
            }
            Claim roles = decodedJwt.getClaim("roles");
            if (!roles.isNull()) {
                this.roles = Arrays.stream(roles.asArray(String.class))
                        .map(Role::from)
                        .toArray(Role[]::new);
            }
            this.iat = decodedJwt.getIssuedAt();
            this.exp = decodedJwt.getExpiresAt();
        }

        public static Claims from(Long memberId, Role[] roles) {
            Claims claims = new Claims();
            claims.userId = memberId;
            claims.roles = roles;
            return claims;
        }

        @Override
        public String toString() {
            return "Claims{"
                    + "userId=" + userId
                    + ", roles="
                    + Arrays.toString(roles)
                    + ", iat=" + iat
                    + ", exp="
                    + exp
                    + '}';
        }
    }
}
