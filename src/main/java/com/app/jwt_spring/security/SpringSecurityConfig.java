package com.app.jwt_spring.security;

import com.app.jwt_spring.filters.JwtAuthenticationFilter;
import com.app.jwt_spring.filters.JwtAuthorizationFilter;
import com.app.jwt_spring.services.JwtService;
import com.app.jwt_spring.utils.RolEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final AccessDeniedHandler accessDeniedHandler;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final ObjectMapper objectMapper;

    private final JwtService jwtService;

    public SpringSecurityConfig(@Qualifier("jwtAuthorizationAccesDenied") AccessDeniedHandler accessDeniedHandler,
                                AuthenticationConfiguration authenticationConfiguration, ObjectMapper objectMapper, JwtService jwtService) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationConfiguration = authenticationConfiguration;
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() throws Exception {
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        final var jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, this.objectMapper, this.jwtService);
        final var jwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, this.objectMapper, this.jwtService);
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.POST, "/users/create")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority(RolEnum.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "users/update-permissions").hasAuthority(RolEnum.ROLE_ADMIN.name())
                        .anyRequest().authenticated())
                .exceptionHandling(handler -> handler.accessDeniedHandler(this.accessDeniedHandler))
                .addFilter(jwtAuthenticationFilter)
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable).build();
    }

}
