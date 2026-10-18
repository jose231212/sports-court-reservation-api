package com.reservas.apidereservas.dto.court;

import java.math.BigDecimal;

import com.reservas.apidereservas.entity.enums.CourtType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CourtRequestDTO {

    @Schema(
        description = "Name of the court",
        example = "Court A",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 20
    )
    @NotBlank 
    @Size(max = 20)
    private String name;

    @Schema(
        description = "Additional details about the court",
        example = "Indoor bastketball court",
        maxLength = 150
    )
    @Size(max = 150)
    private String description;

    @Schema(
        description = "Physical location of the court",
        example = "Santo Domingo, RD",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String location;

    @Schema(
        description = "Type of sport supported by the court",
        example = "Baloncesto",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private CourtType type;

    @Schema(
        description = "Hourly rental price of the court",
        example = "1500.00",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0",
        exclusiveMinimum = true
    )
    @NotNull 
    @Positive
    private BigDecimal pricePerHour;

}
