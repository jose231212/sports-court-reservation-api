package com.reservas.apidereservas.dto.user;

import java.time.LocalDateTime;

import com.reservas.apidereservas.entity.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class UserResponseDTO {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "User's full name", example = "Pedro Fernandez")
    private String name;

    @Schema(description = "User's email address", example = "pedro@gmail.com")
    private String email;

    @Schema(description = "Indicates wheter the user accout is active", example = "true")
    private boolean active;
    
    @Schema(description = "Date and time when the user account was created", example = "2026-10-18T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Role assigned to the user", example = "USER")
    private Role role;

}
