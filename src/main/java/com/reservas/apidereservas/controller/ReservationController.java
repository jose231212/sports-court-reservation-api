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

import com.reservas.apidereservas.dto.ErrorResponseDTO;
import com.reservas.apidereservas.dto.reservation.ReservationRequestDTO;
import com.reservas.apidereservas.dto.reservation.ReservationResponseDTO;
import com.reservas.apidereservas.service.reservation.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reservations", description = "Endpoints for reservation management")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Create reservation", description = "Create a new reservation using the provided details, provided that the corresponding validations are met.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or reservation rules violated",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Court or associated user not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "An unexpected internal server error occurred",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(value = """
                            {
                              "status": 500,
                              "message": "Error interno del servidor",
                              "timestamp": "2026-10-22T10:30:00",
                              "errors": null
                            }
                            """)))
    })
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {

        ReservationResponseDTO reservationResponseDTO = reservationService.createReservation(reservationRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDTO);
    }

    @Operation(summary = "Find reservation by ID", description = "Retrieves a reservation using its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation found successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(value = """
                            {
                              "status": 500,
                              "message": "Error interno del servidor",
                              "timestamp": "2026-10-22T10:30:00",
                              "errors": null
                            }
                            """))
            )
    })
    @GetMapping("/{id}")
    public ReservationResponseDTO getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @Operation(summary = "Retrieve all reservation", description = "Retrieves all reservations using paginated results")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(value = """
                            {
                              "status": 500,
                              "message": "Error interno del servidor",
                              "timestamp": "2026-10-22T10:30:00",
                              "errors": null
                            }
                            """))
            )
    })
    @GetMapping
    public Page<ReservationResponseDTO> getAllReservations(Pageable pageable) {
        return reservationService.getAllReservations(pageable);
    }

    @Operation(summary = "Update reservation", description = "Updates an existing reservation using its unique identifier and the provided reservation details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or reservation rules violated",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Reservation or requested court not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(value = """
                            {
                              "status": 500,
                              "message": "Error interno del servidor",
                              "timestamp": "2026-10-22T10:30:00",
                              "errors": null
                            }
                            """)))
    })
    @PutMapping("/{id}")
    public ReservationResponseDTO updateReservation(@PathVariable Long id,
           @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {
        return reservationService.updateReservation(id, reservationRequestDTO);
    }

    @Operation(summary = "Delete reservation by ID", description = "Deletes an existing reservation using its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reservation deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class), examples = @ExampleObject(value = """
                            {
                              "status": 500,
                              "message": "Error interno del servidor",
                              "timestamp": "2026-10-22T10:30:00",
                              "errors": null
                            }
                            """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable Long id) {

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }

}
