package com.app.jwt_spring.repositories;

import com.app.jwt_spring.entities.RolEntity;
import com.app.jwt_spring.utils.RolEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RolEntity,Long> {

    Optional<RolEntity> findByName(RolEnum name);

}
