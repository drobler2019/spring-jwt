package com.app.jwt_spring.controllers;

import com.app.jwt_spring.dto.UserResponseDTO;
import com.app.jwt_spring.dto.UserRequestDTO;
import com.app.jwt_spring.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.function.Function;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = {"application/hal+json"})
    public ResponseEntity<CollectionModel<UserResponseDTO>> list() {
        var all = this.userService.findAll().stream().map(getUserDTOUserDTOFunction()).toList();
        return ResponseEntity.ok(CollectionModel.of(all));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(HttpServletRequest httpServletRequest, @RequestBody UserRequestDTO userRequestDTO) {
        var response = this.userService.saveUser(userRequestDTO);
        return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI())).body(response);
    }

    private  Function<UserResponseDTO, UserResponseDTO> getUserDTOUserDTOFunction() {
        return user -> {
            var link = WebMvcLinkBuilder.linkTo(UserRestController.class, HttpMethod.GET).withSelfRel();
            user.add(link);
            return user;
        };
    }

}
