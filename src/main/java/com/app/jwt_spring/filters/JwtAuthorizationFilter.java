package com.app.jwt_spring.filters;

import com.app.jwt_spring.services.JwtService;
import com.app.jwt_spring.utils.AbstractCustomSimpleGrantedAuthority;
import com.app.jwt_spring.utils.exceptionUtil.HateoasUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.stream.Stream;

import static com.app.jwt_spring.utils.Jwtconstant.*;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper,JwtService jwtService) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
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
            var claims = this.jwtService.extractAllClaims(token);
            var username = (String) claims.get("username");
            var authoritiesClaims = (String) claims.get("authorities");
            var values = this.objectMapper.addMixIn(SimpleGrantedAuthority.class, AbstractCustomSimpleGrantedAuthority.class).readValue(authoritiesClaims, SimpleGrantedAuthority[].class);
            var authorities = Stream.of(values).toList();
            var usernameAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(usernameAuthenticationToken);
            chain.doFilter(request, response);
        } catch (JwtException jwtException) {
            var problem = HateoasUtil.buildProblem(request.getRequestURI(), HttpStatus.UNAUTHORIZED, jwtException);
            response.getWriter().write(this.objectMapper.writeValueAsString(problem));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(CONTENT_TYPE);
        }


    }
}
