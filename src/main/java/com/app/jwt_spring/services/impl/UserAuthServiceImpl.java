package com.app.jwt_spring.services.impl;

import com.app.jwt_spring.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAuthServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserAuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userEntity = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("usuario no encontrado"));
        var authoritesEntity = userEntity.getRoles().stream().map(rol -> new SimpleGrantedAuthority(rol.getName().name())).toList();
        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(authoritesEntity).build();
    }
}
