package com.app.jwt_spring.mapper;

import com.app.jwt_spring.dto.UserDTO;
import com.app.jwt_spring.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity convertDTOToEntity (UserDTO userDTO);

    @Mapping(target = "password", ignore = true)
    UserDTO convertEntityToDTO(UserEntity userEntity);

}
