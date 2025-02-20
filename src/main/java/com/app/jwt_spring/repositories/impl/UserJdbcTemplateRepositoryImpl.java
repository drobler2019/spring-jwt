package com.app.jwt_spring.repositories.impl;

import com.app.jwt_spring.handler.StoredProcedureException;
import com.app.jwt_spring.repositories.UserJdbcTemplateRepository;
import com.app.jwt_spring.utils.RolEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

@Repository
public class UserJdbcTemplateRepositoryImpl implements UserJdbcTemplateRepository {


    private static final Logger log = LoggerFactory.getLogger(UserJdbcTemplateRepositoryImpl.class);
    private static final String SP_RESPONSE_OK = "OK";
    private static final String SP_RESPONSE_ERROR = "ERROR:";
    private final JdbcTemplate jdbcTemplate;
    private final static String STORED_PROCEDURE_NAME = "{call guardar_usuario(?, ?, ?, ?)}";

    public UserJdbcTemplateRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public String insertUser(String username, String password) throws SQLException {
        try {
            CallableStatementCreator callableStatementCreator = con -> {
                var cs = con.prepareCall(STORED_PROCEDURE_NAME);
                cs.setString(1, username);
                cs.setString(2, password);
                cs.setInt(3, RolEnum.ROLE_INVITED.getId());
                cs.registerOutParameter(4, Types.VARCHAR);
                return cs;
            };
            return this.jdbcTemplate.execute(callableStatementCreator, this::getString);
        } catch (DataAccessException e) {
            log.error("insertUser: {}",e.getMessage());
            if (e.getMessage().contains(SP_RESPONSE_ERROR)) {
                var message = e.getMessage().substring(e.getMessage().indexOf(SP_RESPONSE_ERROR));
                throw new StoredProcedureException(message);
            }
            throw new SQLException(e.getMessage());
        }
    }

    private  String getString(CallableStatement cs) throws SQLException {
        cs.executeUpdate();
        var response = cs.getString(4);
        if (!response.equals(SP_RESPONSE_OK)) {
            throw new SQLException(response);
        }
        return response;
    }

}
