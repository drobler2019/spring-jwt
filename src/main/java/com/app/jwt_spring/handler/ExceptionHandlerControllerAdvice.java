package com.app.jwt_spring.handler;

import com.app.jwt_spring.handler.exception.GenericException;
import com.app.jwt_spring.handler.exception.StoredProcedureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.app.jwt_spring.utils.exceptionUtil.HateoasUtil.buildProblem;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Problem> badCredentialsException(HttpServletRequest request, AccessDeniedException accessDeniedException) {
        var problem = buildProblem(request.getRequestURI(), HttpStatus.FORBIDDEN, accessDeniedException);
        return new ResponseEntity<>(problem, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Problem> duplicateUsernameException(HttpServletRequest request, RuntimeException runtimeException) {
        var response = buildProblem(request.getRequestURI(), HttpStatus.BAD_REQUEST, runtimeException);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Problem> sqlException(HttpServletRequest request, SQLException sqlException) {
        return this.internalServerError(request,sqlException);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<Problem> genericException(HttpServletRequest request, GenericException exception) {
        return this.internalServerError(request,exception);
    }

    @ExceptionHandler(StoredProcedureException.class)
    public ResponseEntity<Problem> storedProcedureExceptionGeneric(HttpServletRequest request, DataAccessException dataAccessException) {
        var response = buildProblem(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, dataAccessException);
        if (dataAccessException instanceof StoredProcedureException storedProcedureException) {
            return this.storedProcedureException(request, storedProcedureException);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Problem> storedProcedureException(HttpServletRequest request, StoredProcedureException dataAccessException) {
        var response = buildProblem(request.getRequestURI(), HttpStatus.CONFLICT, dataAccessException);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Problem> internalServerError(HttpServletRequest request, Exception exception) {
        var response = buildProblem(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, exception);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
