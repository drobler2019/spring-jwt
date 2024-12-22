package com.app.jwt_spring.services.impl;

import com.app.jwt_spring.dto.UserResponseDTO;
import com.app.jwt_spring.dto.UserRequestDTO;
import com.app.jwt_spring.entities.RolEntity;
import com.app.jwt_spring.mapper.UserMapper;
import com.app.jwt_spring.repositories.RoleRepository;
import com.app.jwt_spring.repositories.UserRepository;
import com.app.jwt_spring.services.UserService;
import com.app.jwt_spring.utils.RolEnum;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final boolean USER_ACTIVE = true;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return this.userRepository.findAll().stream().map(this.userMapper::convertEntityToDTO).toList();
    }

    @Override
    @Transactional
    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {
        if (this.userRepository.existsByUsername(userRequestDTO.username())) {
            throw new DataIntegrityViolationException("el usuario ya está en uso!");
        }
        var rol = this.roleRepository.findByName(RolEnum.ROLE_USER).get();
        var roles = new HashSet<RolEntity>();
        roles.add(rol);
        var userEntity = this.userMapper.convertDTOToEntity(userRequestDTO);
        userEntity.setRoles(roles);
        userEntity.setEnabled(USER_ACTIVE);
        userEntity.setPassword(this.passwordEncoder.encode(userEntity.getPassword()));
        return this.userMapper.convertEntityToDTO(this.userRepository.save(userEntity));
    }
}
