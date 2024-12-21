package com.app.jwt_spring.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDTO(String username, String password, Boolean enabled, Boolean isAdmin, Set<RolDTO> roles,
                      LocalDateTime createAt) {
}
