package com.app.jwt_spring.services.impl;

import com.app.jwt_spring.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private Key SECRET_KEY = Jwts.SIG.HS256.key().build();

    @Override
    public String createToken(String username, Map<String, String> claims) {
        return Jwts.builder()
                .subject(username)
                .signWith(SECRET_KEY)
                .expiration(new Date(System.currentTimeMillis() + 300000))
                .issuedAt(new Date())
                .claims(claims)
                .compact();
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

}
