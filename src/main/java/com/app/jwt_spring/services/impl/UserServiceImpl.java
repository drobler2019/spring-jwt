package com.app.jwt_spring.services.impl;

import com.app.jwt_spring.dto.UserDTO;
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
    public List<UserDTO> findAll() {
        return this.userRepository.findAll().stream().map(this.userMapper::convertEntityToDTO).toList();
    }

    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        if (this.userRepository.existsByUsername(userDTO.username())) {
            throw new DataIntegrityViolationException("el usuario ya está en uso!");
        }
        var rolValue = userDTO.isAdmin() ? RolEnum.ROLE_ADMIN : RolEnum.ROLE_USER;
        var optionalRol = this.roleRepository.findByName(rolValue);
        var roles = new HashSet<RolEntity>();
        optionalRol.ifPresent(roles::add);
        var userEntity = this.userMapper.convertDTOToEntity(userDTO);
        userEntity.setRoles(roles);
        userEntity.setPassword(this.passwordEncoder.encode(userEntity.getPassword()));
        return this.userMapper.convertEntityToDTO(this.userRepository.save(userEntity));
    }
}
