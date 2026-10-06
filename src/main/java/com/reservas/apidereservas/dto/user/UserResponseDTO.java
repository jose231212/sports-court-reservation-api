package com.reservas.apidereservas.dto.user;

import java.time.LocalDateTime;

import com.reservas.apidereservas.entity.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class UserResponseDTO {

    private Long id;

    private String name;

    private String email;

    private boolean active;
    
    private LocalDateTime createdAt;

    private Role role;

}
