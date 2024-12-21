package com.app.jwt_spring.security;

import io.jsonwebtoken.Jwts;

import java.security.Key;

public class JwtConfig {
    public static Key SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String CONTENT_TYPE = "application/json";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
}
