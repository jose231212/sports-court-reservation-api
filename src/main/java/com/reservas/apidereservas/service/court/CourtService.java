package com.reservas.apidereservas.service.court;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.reservas.apidereservas.dto.court.CourtRequestDTO;
import com.reservas.apidereservas.dto.court.CourtResponseDTO;

public interface CourtService {

    CourtResponseDTO createCourt(CourtRequestDTO courtRequestDTO);

    CourtResponseDTO getCourtById(Long id);

    Page<CourtResponseDTO> getAllCourts(Pageable pageable);

    CourtResponseDTO updateCourt(Long id, CourtRequestDTO courtRequestDTO);

    void deleteCourt(Long id);

}
