package com.app.jwt_spring.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> badCredentialsException(AccessDeniedException accessDeniedException) {
        var user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var messageException = String.format("%s for %s", accessDeniedException.getMessage(), user);
        var response = Map.of("title", HttpStatus.FORBIDDEN.getReasonPhrase(), "code", String.valueOf(HttpStatus.FORBIDDEN.value()), "error", messageException);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> duplicateUsernameException(DataIntegrityViolationException dataIntegrityViolationException) {
        var response = Map.of("title", HttpStatus.BAD_REQUEST.getReasonPhrase(), "code", String.valueOf(HttpStatus.BAD_REQUEST.value()), "error", dataIntegrityViolationException.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
