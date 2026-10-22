package com.reservas.apidereservas.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.apidereservas.dto.ErrorResponseDTO;
import com.reservas.apidereservas.dto.court.CourtRequestDTO;
import com.reservas.apidereservas.dto.court.CourtResponseDTO;
import com.reservas.apidereservas.service.court.CourtService;

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

@Tag(name = "Courts", description = "Endpoints for creating, retrieving, updating, and deleting courts")
@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class CourtController {

    private final CourtService courtService;

    @Operation(summary = "Create court", description = "Creates a new court using the provided court details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Court created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or court rules violated"),
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourtResponseDTO> createCourt(@Valid @RequestBody CourtRequestDTO courtRequestDTO) {

        CourtResponseDTO courtResponseDTO = courtService.createCourt(courtRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(courtResponseDTO);
    }

    @Operation(summary = "Get court by ID", description = "Retrieves a court using its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Court retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Court not found",
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
    public CourtResponseDTO getCourtById(@PathVariable Long id) {
        return courtService.getCourtById(id);
    }

    @Operation(summary = "Get all courts", description = "Retrieves all courts using paginated results")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
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
    public Page<CourtResponseDTO> getAllCourts(Pageable pageable) {
        return courtService.getAllCourts(pageable);
    }

    @Operation(summary = "Update court", description = "Updates an existing court using its unique identifier and the provided court details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Court updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or court rules violated"),
            @ApiResponse(responseCode = "404", description = "Court not found",
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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CourtResponseDTO updateCourt(@PathVariable Long id, @Valid @RequestBody CourtRequestDTO courtRequestDTO) {
        return courtService.updateCourt(id, courtRequestDTO);
    }

    @Operation(summary = "Delete court by ID", description = "Deletes an existing court using its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Court deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Court not found",
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourtById(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.noContent().build();
    }
}
