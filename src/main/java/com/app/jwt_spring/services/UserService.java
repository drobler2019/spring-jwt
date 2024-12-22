package com.app.jwt_spring.services;

import com.app.jwt_spring.dto.UserResponseDTO;
import com.app.jwt_spring.dto.UserRequestDTO;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> findAll();
    UserResponseDTO saveUser(UserRequestDTO requestDTO);

}
