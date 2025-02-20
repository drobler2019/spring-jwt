package com.app.jwt_spring.services;

import com.app.jwt_spring.dto.UserRequestDTO;
import com.app.jwt_spring.dto.UserResponseDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserService {

    List<UserResponseDTO> findAll();

    UserResponseDTO saveUser(UserRequestDTO requestDTO);

    UserResponseDTO updatePermissions(String username, List<String> roles);

    UserResponseDTO deletePermissions(String username, List<String> roles);

    String saveUserWithJdbcTemplate(UserRequestDTO requestDTO) throws SQLException;

}
