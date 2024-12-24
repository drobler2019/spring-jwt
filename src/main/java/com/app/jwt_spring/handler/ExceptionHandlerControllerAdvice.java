package com.app.jwt_spring.handler;

import com.app.jwt_spring.utils.exceptionUtil.HateoasUtl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Problem> badCredentialsException(HttpServletRequest request, AccessDeniedException accessDeniedException) {
        var problem = HateoasUtl.buildProblem(request.getRequestURI(), HttpStatus.FORBIDDEN, accessDeniedException);
        return new ResponseEntity<>(problem, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Problem> duplicateUsernameException(HttpServletRequest request, RuntimeException runtimeException) {
        var response = HateoasUtl.buildProblem(request.getRequestURI(), HttpStatus.BAD_REQUEST, runtimeException);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
