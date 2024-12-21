package com.app.jwt_spring.repositories;

import com.app.jwt_spring.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {}
