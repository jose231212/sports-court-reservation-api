package com.reservas.apidereservas.dto.court;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.reservas.apidereservas.entity.enums.CourtType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CourtResponseDTO {

    @Schema(description = "Unique identifier of the court", example = "1")
    private Long id;

    @Schema(description = "Name of the court", example = "Court A")
    private String name;

    @Schema(description = "Additonal details of court", example = "Indoor basketball court")
    private String description;
    
    @Schema(description = "Physical location of the court", example = "Santiago, RD")
    private String location;

    @Schema(description = "Type of supported by the court", example = "BALONCESTO")
    private CourtType type;

    @Schema(description = "Hourly rental price of the court", example = "1500.00")
    private BigDecimal pricePerHour;

    @Schema(description = "Indicates wheter the court is active", example = "true")
    private boolean active;

    @Schema(description = "Date an time when the court was created", example = "2026-10-18T14:30:00")
    private LocalDateTime createdAt;

}
