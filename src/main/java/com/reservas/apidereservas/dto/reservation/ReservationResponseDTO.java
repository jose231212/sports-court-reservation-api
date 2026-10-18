package com.reservas.apidereservas.dto.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.reservas.apidereservas.entity.enums.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ReservationResponseDTO {

    @Schema(description = "Unique identifier of the reservation", example = "1")
    private Long id;
    
    @Schema(description = "Date of reservation", example = "2026-11-15")
    private LocalDate date;

    @Schema(description = "Reservation start time", example = "14:00:00")
    private LocalTime startTime;

    @Schema(description = "Reservation end time", example = "15:00:00")
    private LocalTime endTime;

    @Schema(description = "Current status of the reservation", example = "PENDING")
    private ReservationStatus status;

    @Schema(description = "Date and time when the reservation was created", example = "2026-10-18T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "ID of the user who made the reservation", example = "1")
    private Long userId;

    @Schema(description = "ID of the reserved court", example = "1")
    private Long courtId;

}
