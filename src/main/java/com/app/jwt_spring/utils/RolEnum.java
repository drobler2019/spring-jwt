package com.app.jwt_spring.utils;

public enum RolEnum {
    ROLE_ADMIN(1),
    ROLE_USER(2),
    ROLE_INVITED(3);

    private final Integer id;

    RolEnum(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
