package com.reservas.apidereservas.dto;

import java.time.LocalDateTime;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ErrorResponseDTO {
    
    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Description of the error")
    private String message;

    @Schema(
        description = "Date and time when error ocurred"
    )
    private LocalDateTime timestamp;

    @Schema(
        description = "Details of field validation error, when applicable"
    )
    private Map<String, String> errors;

}
