package com.reservas.apidereservas.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ReservationRequestDTO {

    @NotNull 
    private LocalDate date;

    @NotNull 
    private LocalTime startTime;

    @NotNull 
    private LocalTime endTime;

    @NotNull 
    private Long courtId;

}
