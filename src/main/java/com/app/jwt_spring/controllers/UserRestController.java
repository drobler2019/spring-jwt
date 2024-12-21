package com.app.jwt_spring.controllers;

import com.app.jwt_spring.dto.UserDTO;
import com.app.jwt_spring.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> list() {
        return ResponseEntity.ok(this.userService.findAll());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(HttpServletRequest httpServletRequest, @RequestBody UserDTO userDTO) {
        var response = this.userService.saveUser(userDTO);
        return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI()))
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(HttpServletRequest httpServletRequest, @RequestBody UserDTO userDTO) {
        var response = this.userService.saveUser(userDTO);
        return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI()))
                .body(response);
    }
}
