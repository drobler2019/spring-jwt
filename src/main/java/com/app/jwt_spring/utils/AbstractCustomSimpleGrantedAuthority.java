package com.app.jwt_spring.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractCustomSimpleGrantedAuthority {

    @JsonCreator
    public AbstractCustomSimpleGrantedAuthority(@JsonProperty("authority") String role) {}

}
