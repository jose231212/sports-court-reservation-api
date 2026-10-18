package com.reservas.apidereservas.dto;

import java.time.LocalDateTime;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ErrorResponseDTO {
    
    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Description of the error", example = "Cancha no encontrada con id: 1")
    private String message;

    @Schema(
        description = "Date and time when error ocurred",
        example = "2026-10-18T14:30:00"
    )
    private LocalDateTime timestamp;

    @Schema(
        description = "Details of field validation error, when applicable",
        example = "{\"name\":\"must not be blank\"}"
    )
    private Map<String, String> errors;

}
