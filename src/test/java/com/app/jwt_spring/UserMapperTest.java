package com.app.jwt_spring;

import com.app.jwt_spring.dto.UserRequestDTO;
import com.app.jwt_spring.entities.UserEntity;
import com.app.jwt_spring.mapper.UserMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    public void convertDTOtoEntityTest() {
        var userRequestDTO = new UserRequestDTO("droble@example.com.co", "d2019");
        var userEntity = this.userMapper.convertDTOToEntity(userRequestDTO);
        assertAll(() -> assertEquals(userRequestDTO.username(), userEntity.getUsername()),
                () -> assertEquals(userRequestDTO.password(), userEntity.getPassword()));
    }

    @Test
    public void convertEntityToDTOTest() {
        var userEntity = new UserEntity();
        userEntity.setUsername("drobler2018@gmail.com");
        userEntity.setPassword("12345");
        var userResponseDTO = this.userMapper.convertEntityToDTO(userEntity);
        assertEquals(userResponseDTO.getUsername(),userEntity.getUsername());
    }

}
