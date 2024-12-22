package com.app.jwt_spring.filters;

import com.app.jwt_spring.dto.SessionRequestDTO;
import com.app.jwt_spring.dto.TokenResponseDTO;
import com.app.jwt_spring.security.JwtConfig;
import com.app.jwt_spring.utils.exceptionUtil.HateoasUtl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;

import static com.app.jwt_spring.security.JwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        SessionRequestDTO sessionRequestDTO;
        try {
            sessionRequestDTO = this.objectMapper.readValue(request.getInputStream(), SessionRequestDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(sessionRequestDTO.username(), sessionRequestDTO.password());
        return this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        var username = ((User) authResult.getPrincipal()).getUsername();
        var authorities = authResult.getAuthorities();
        var builderClaims = new HashMap<String, String>();
        builderClaims.put("authorities", this.objectMapper.writeValueAsString(authorities));
        builderClaims.put("username", username);
        String token = jwtConfig.createToken(username, builderClaims);
        response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + token);
        var tokenResponse = new TokenResponseDTO(token);
        response.getWriter().write(this.objectMapper.writeValueAsString(tokenResponse));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        var problem = HateoasUtl.buildProblem(request.getRequestURI(), HttpStatus.UNAUTHORIZED, failed);
        response.getWriter().write(this.objectMapper.writeValueAsString(problem));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(CONTENT_TYPE);
    }
}
