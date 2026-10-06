package com.reservas.apidereservas.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.reservas.apidereservas.entity.enums.CourtType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter
@NoArgsConstructor 
public class Court {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    private CourtType type;

    private BigDecimal pricePerHour;

    private boolean active;

    private LocalDateTime createdAt;

}
