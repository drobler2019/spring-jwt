package com.app.jwt_spring.utils.exceptionUtil;

import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class HateoasUtil {

    public static Problem buildProblem(String uri, HttpStatus httpStatus, RuntimeException runtimeException) {
        return Problem.create()
                .withTitle(httpStatus.getReasonPhrase())
                .withStatus(httpStatus)
                .withDetail(runtimeException.getMessage())
                .withInstance(URI.create(uri));
    }

}
