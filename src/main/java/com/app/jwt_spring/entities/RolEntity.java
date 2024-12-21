package com.app.jwt_spring.entities;

import com.app.jwt_spring.utils.RolEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class RolEntity extends SuperEntity {

    @Enumerated(EnumType.STRING)
    private RolEnum name;

    public RolEnum getName() {
        return name;
    }

    public void setName(RolEnum name) {
        this.name = name;
    }
}
