package com.app.jwt_spring.handler;

import org.springframework.dao.DataAccessException;

public class StoredProcedureException extends DataAccessException {
    public StoredProcedureException(String msg) {
        super(msg);
    }
}
