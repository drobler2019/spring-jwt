package com.app.jwt_spring.handler;

import com.app.jwt_spring.utils.exceptionUtil.HateoasUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Problem> badCredentialsException(HttpServletRequest request, AccessDeniedException accessDeniedException) {
        var problem = HateoasUtil.buildProblem(request.getRequestURI(), HttpStatus.FORBIDDEN, accessDeniedException);
        return new ResponseEntity<>(problem, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Problem> duplicateUsernameException(HttpServletRequest request, RuntimeException runtimeException) {
        var response = HateoasUtil.buildProblem(request.getRequestURI(), HttpStatus.BAD_REQUEST, runtimeException);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Problem> sqlException(HttpServletRequest request, SQLException sqlException) {
        var response = HateoasUtil.buildProblem(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, sqlException);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StoredProcedureException.class)
    public ResponseEntity<Problem> storedProcedureExceptionGeneric(HttpServletRequest request, DataAccessException dataAccessException) {
        var response = HateoasUtil.buildProblem(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, dataAccessException);
        if (dataAccessException instanceof StoredProcedureException storedProcedureException) {
            return this.storedProcedureException(request, storedProcedureException);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<Problem> storedProcedureException(HttpServletRequest request, StoredProcedureException dataAccessException) {
        var response = HateoasUtil.buildProblem(request.getRequestURI(), HttpStatus.CONFLICT, dataAccessException);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


}
