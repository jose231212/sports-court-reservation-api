package com.reservas.apidereservas.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class AuthResponseDTO {

    private String token;

    private String tokenType;

    private long expiresIn;

}
