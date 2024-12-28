package com.app.jwt_spring.services;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JwtService {

    String createToken(String username, Map<String, String> claims);

    Claims extractAllClaims(String token);

}
