package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.annotation.Loggable;
import org.example.controller.dto.LogInDTO;
import org.example.controller.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Loggable
@Tag(name = "Log In Controller")
@RestController
@RequestMapping("/login")
public class LogInController {
    UserService userService;

    @Autowired
    public LogInController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get logged in user")
    @PostMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getLoggedInUser(@RequestBody LogInDTO logInDTO) {
        UserDTO userDTO = userService.findUserByEmailAndPassword(logInDTO.getEmail(), logInDTO.getPassword());

        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}