package com.reservas.apidereservas.dto.court;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.reservas.apidereservas.entity.enums.CourtType;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CourtResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String location;

    private CourtType type;

    private BigDecimal pricePerHour;

    private boolean active;

    private LocalDateTime createdAt;

}
