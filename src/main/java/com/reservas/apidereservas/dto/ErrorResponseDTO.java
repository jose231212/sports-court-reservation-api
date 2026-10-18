package com.reservas.apidereservas.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ErrorResponseDTO {
    
    private int status;

    private String message;

    private LocalDateTime timestamp;

    private Map<String, String> errors;

}
