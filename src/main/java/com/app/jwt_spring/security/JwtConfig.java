package com.app.jwt_spring.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtConfig {

    public static Key SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String CONTENT_TYPE = "application/json";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    public String createToken(String username, Map<String, String> claims) {
        return Jwts.builder()
                .subject(username)
                .signWith(SECRET_KEY)
                .expiration(new Date(System.currentTimeMillis() + 300000))
                .issuedAt(new Date())
                .claims(claims)
                .compact();
    }
}
