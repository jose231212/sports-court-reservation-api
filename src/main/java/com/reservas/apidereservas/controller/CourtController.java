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

import com.reservas.apidereservas.dto.court.CourtRequestDTO;
import com.reservas.apidereservas.dto.court.CourtResponseDTO;
import com.reservas.apidereservas.service.court.CourtService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Court created correctly")
    })
    @PostMapping
    public ResponseEntity<CourtResponseDTO> createCourt(@Valid @RequestBody CourtRequestDTO courtRequestDTO) {

        CourtResponseDTO courtResponseDTO = courtService.createCourt(courtRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(courtResponseDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Court found correctly")
    })
    @GetMapping("/{id}")
    public CourtResponseDTO getCourtById(@PathVariable Long id) {
        return courtService.getCourtById(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courts found correctly")
    })
    @GetMapping
    public Page<CourtResponseDTO> getAllCourts(Pageable pageable) {
        return courtService.getAllCourts(pageable);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Court updated correctly")
    })
    @PutMapping("/{id}")
    public CourtResponseDTO updateCourt(@PathVariable Long id, @Valid @RequestBody CourtRequestDTO courtRequestDTO) {
        return courtService.updateCourt(id, courtRequestDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Court deleted correctly")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourtById(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.noContent().build();
    }
}
