package com.app.jwt_spring.services;

import com.app.jwt_spring.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();
    UserDTO saveUser(UserDTO userDTO);
}
