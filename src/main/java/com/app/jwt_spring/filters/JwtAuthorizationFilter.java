package com.app.jwt_spring.filters;

import com.app.jwt_spring.utils.AbstractCustomSimpleGrantedAuthority;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static com.app.jwt_spring.security.JwtConfig.*;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var header = request.getHeader(HEADER_AUTHORIZATION);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        var token = header.substring(7);

        try {
            var claims = Jwts.parser().verifyWith((SecretKey) SECRET_KEY).build().parseSignedClaims(token).getPayload();
            var username = claims.getSubject();
            var authoritiesClaims =  (String) claims.get("authorities");
            var values = this.objectMapper.addMixIn(SimpleGrantedAuthority.class, AbstractCustomSimpleGrantedAuthority.class).readValue(authoritiesClaims, SimpleGrantedAuthority[].class);
            var authorities = Stream.of(values).toList();
            var usernameAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(usernameAuthenticationToken);
            chain.doFilter(request, response);
        } catch (JwtException e) {
            var responseError = Map.of("error", e.getMessage(), "message", "Token no válido");
            response.getWriter().write(this.objectMapper.writeValueAsString(responseError));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(CONTENT_TYPE);
        }


    }
}
