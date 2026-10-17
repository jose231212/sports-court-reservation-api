package com.reservas.apidereservas.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.apidereservas.dto.reservation.ReservationRequestDTO;
import com.reservas.apidereservas.dto.reservation.ReservationResponseDTO;
import com.reservas.apidereservas.service.reservation.ReservationService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reservation created correctly")
    })
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {

        ReservationResponseDTO reservationResponseDTO = reservationService.createReservation(reservationRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation found correctly")
    })
    @GetMapping("/{id}")
    public ReservationResponseDTO getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations found correctly")
    })
    @GetMapping
    public Page<ReservationResponseDTO> getAllReservations(Pageable pageable) {
        return reservationService.getAllReservations(pageable);
    }

    @ApiResponses(@ApiResponse(responseCode = "200", description = "Reservation updated correctly"))
    @PutMapping("/{id}")
    public ReservationResponseDTO updateReservation(@PathVariable Long id,
           @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {
        return reservationService.updateReservation(id, reservationRequestDTO);
    }

    @ApiResponses(@ApiResponse(responseCode = "204", description = "Reservation deleted correctly"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable Long id) {

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }

}
