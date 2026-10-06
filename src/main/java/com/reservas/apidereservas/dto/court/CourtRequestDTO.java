package com.reservas.apidereservas.dto.court;

import java.math.BigDecimal;

import com.reservas.apidereservas.entity.enums.CourtType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CourtRequestDTO {

    @NotBlank 
    @Size(max = 20)
    private String name;

    @Size(max = 150)
    private String description;

    @NotBlank
    private String location;

    @NotNull
    private CourtType type;

    @NotNull 
    @Positive
    private BigDecimal pricePerHour;

}
