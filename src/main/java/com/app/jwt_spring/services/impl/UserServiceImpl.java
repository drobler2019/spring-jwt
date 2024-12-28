package com.app.jwt_spring.services.impl;

import com.app.jwt_spring.dto.UserRequestDTO;
import com.app.jwt_spring.dto.UserResponseDTO;
import com.app.jwt_spring.entities.RolEntity;
import com.app.jwt_spring.entities.UserEntity;
import com.app.jwt_spring.mapper.UserMapper;
import com.app.jwt_spring.repositories.RoleRepository;
import com.app.jwt_spring.repositories.UserRepository;
import com.app.jwt_spring.services.UserService;
import com.app.jwt_spring.utils.RolEnum;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    @Transactional
    public UserResponseDTO updatePermissions(String username, List<String> roles) {
        this.validePermissions(roles);
        var userEntity = getUserEntity(username);
        var rolesEntity = roles.stream().map(rol -> this.roleRepository.findByName(RolEnum.valueOf(rol))).map(Optional::get).collect(Collectors.toSet());
        userEntity.getRoles().addAll(rolesEntity);
        this.userRepository.save(userEntity);
        return this.userMapper.convertEntityToDTO(userEntity);
    }

    @Override
    @Transactional
    public UserResponseDTO deletePermissions(String username, List<String> roles) {
        this.validePermissions(roles);
        var userEntity = this.getUserEntity(username);
        roles.forEach(rol -> userEntity.getRoles().removeIf(rolEntity -> rolEntity.getName().name().equals(rol)));
        this.userRepository.save(userEntity);
        return this.userMapper.convertEntityToDTO(userEntity);
    }

    private UserEntity getUserEntity(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("usuario no encontrado"));
    }

    private void validePermissions(List<String> roles) {
        var isPresent = roles.stream().allMatch(rol -> Stream.of(RolEnum.values()).anyMatch(rolEnum -> rolEnum.name().equals(rol)));
        if (!isPresent) {
            throw new IllegalArgumentException("nombre de roles no valido");
        }
    }
}
