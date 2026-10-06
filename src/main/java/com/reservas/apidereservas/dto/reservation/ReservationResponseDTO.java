package com.reservas.apidereservas.dto.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.reservas.apidereservas.entity.enums.ReservationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ReservationResponseDTO {

    private Long id;
    
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private ReservationStatus status;

    private LocalDateTime createdAt;

    private Long userId;

    private Long courtId;

}
