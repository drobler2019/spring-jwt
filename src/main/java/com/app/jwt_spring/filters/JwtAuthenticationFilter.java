package com.app.jwt_spring.filters;

import com.app.jwt_spring.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.app.jwt_spring.security.JwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserDTO userDTO;
        try {
            userDTO = this.objectMapper.readValue(request.getInputStream(), UserDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password());
        return this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        var username = ((User) authResult.getPrincipal()).getUsername();
        var authorities = authResult.getAuthorities();
        var builderClaims = new HashMap<String, String>();
        builderClaims.put("authorities", this.objectMapper.writeValueAsString(authorities));
        builderClaims.put("username", username);

        String token = Jwts.builder()
                .subject(username)
                .signWith(SECRET_KEY)
                .expiration(new Date(System.currentTimeMillis() + 300000))
                .issuedAt(new Date())
                .claims(builderClaims)
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + token);
        var body = new HashMap<String, String>();
        body.put("username", username);
        body.put("message", String.format("Hola %s has iniciado sesión correctamente!", username));
        body.put("token", token);
        response.getWriter().write(this.objectMapper.writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        var responseError = Map.of("message", "Error usuario o contraseña incorrectos", "error", failed.getMessage());
        response.getWriter().write(this.objectMapper.writeValueAsString(responseError));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(CONTENT_TYPE);
    }
}
