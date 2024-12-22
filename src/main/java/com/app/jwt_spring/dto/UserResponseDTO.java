package com.app.jwt_spring.dto;

import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponseDTO extends RepresentationModel<UserResponseDTO> {

    private String username;
    private Set<RolDTO> roles;
    private LocalDateTime createAt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<RolDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RolDTO> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

}
