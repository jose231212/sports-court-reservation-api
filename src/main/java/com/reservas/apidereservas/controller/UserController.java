package com.reservas.apidereservas.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.apidereservas.dto.user.UserRequestDTO;
import com.reservas.apidereservas.dto.user.UserResponseDTO;
import com.reservas.apidereservas.service.user.UserService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created correctly")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {

        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found correctly")
    })
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users found correctly")
    })
    @GetMapping
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated correctly")
    })
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(id, userRequestDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted correctly")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
