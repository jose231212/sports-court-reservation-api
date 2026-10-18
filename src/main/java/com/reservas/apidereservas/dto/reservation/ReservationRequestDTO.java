package com.reservas.apidereservas.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ReservationRequestDTO {

    @Schema(
        description = "Date of the reservation",
        example = "2026-11-15",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull 
    private LocalDate date;

    @Schema(
        description = "Reservation start time",
        example = "14:00:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull 
    private LocalTime startTime;

    @Schema(
        description = "Reservation end time",
        example = "15:00:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull 
    private LocalTime endTime;

    @Schema(
        description = "ID of court to be reserved",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull 
    private Long courtId;

}
