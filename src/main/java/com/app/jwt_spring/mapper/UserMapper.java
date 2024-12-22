package com.app.jwt_spring.mapper;

import com.app.jwt_spring.dto.UserResponseDTO;
import com.app.jwt_spring.dto.UserRequestDTO;
import com.app.jwt_spring.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity convertDTOToEntity (UserRequestDTO userRequestDTO);
    UserResponseDTO convertEntityToDTO(UserEntity userEntity);

}
