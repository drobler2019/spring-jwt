package com.app.jwt_spring.repositories;

import java.sql.SQLException;

public interface UserJdbcTemplateRepository {
    String insertUser(String username, String password) throws SQLException;
}
