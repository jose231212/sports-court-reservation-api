package com.reservas.apidereservas.service.court;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.reservas.apidereservas.dto.court.CourtRequestDTO;
import com.reservas.apidereservas.dto.court.CourtResponseDTO;
import com.reservas.apidereservas.entity.Court;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.CourtRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CourtServiceImpl implements CourtService{

    private final CourtRepository courtRepository;

    @Override
    public CourtResponseDTO createCourt(CourtRequestDTO courtRequestDTO) {
        
        Court court = new Court();

        court.setName(courtRequestDTO.getName());
        court.setDescription(courtRequestDTO.getDescription());
        court.setLocation(courtRequestDTO.getLocation());
        court.setType(courtRequestDTO.getType());
        court.setPricePerHour(courtRequestDTO.getPricePerHour());
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());

        Court savedCourt = courtRepository.save(court);

        CourtResponseDTO courtResponseDTO = new CourtResponseDTO();

        courtResponseDTO.setId(savedCourt.getId());
        courtResponseDTO.setName(savedCourt.getName());
        courtResponseDTO.setDescription(savedCourt.getDescription());
        courtResponseDTO.setLocation(savedCourt.getLocation());
        courtResponseDTO.setType(savedCourt.getType());
        courtResponseDTO.setPricePerHour(savedCourt.getPricePerHour());
        courtResponseDTO.setActive(savedCourt.isActive());
        courtResponseDTO.setCreatedAt(savedCourt.getCreatedAt());

        return courtResponseDTO;

    }

    @Override
    public CourtResponseDTO getCourtById(Long id) {
        
        Court court = courtRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Cancha no encontrada con id: " + id
                        )
                    );
                    
        CourtResponseDTO courtResponseDTO = new CourtResponseDTO();

        courtResponseDTO.setId(court.getId());
        courtResponseDTO.setName(court.getName());
        courtResponseDTO.setDescription(court.getDescription());
        courtResponseDTO.setLocation(court.getLocation());
        courtResponseDTO.setType(court.getType());
        courtResponseDTO.setPricePerHour(court.getPricePerHour());
        courtResponseDTO.setActive(court.isActive());
        courtResponseDTO.setCreatedAt(court.getCreatedAt());

        return courtResponseDTO;

    }

    @Override
    public Page<CourtResponseDTO> getAllCourts(Pageable pageable) {
        
        Page<Court> courts = courtRepository.findAll(pageable);

        return courts.map(court -> {

            CourtResponseDTO courtResponseDTO = new CourtResponseDTO();

            courtResponseDTO.setId(court.getId());
            courtResponseDTO.setName(court.getName());
            courtResponseDTO.setDescription(court.getDescription());
            courtResponseDTO.setLocation(court.getLocation());
            courtResponseDTO.setType(court.getType());
            courtResponseDTO.setPricePerHour(court.getPricePerHour());
            courtResponseDTO.setActive(court.isActive());
            courtResponseDTO.setCreatedAt(court.getCreatedAt());

            return courtResponseDTO;

        });

    }

    @Override
    public CourtResponseDTO updateCourt(Long id, CourtRequestDTO courtRequestDTO) {
        
        Court court = courtRepository.findById(id)
                    .orElseThrow(() -> 
                        new ResourceNotFoundException("Cancha no encontrada con id: " + id)
                    );

        court.setName(courtRequestDTO.getName());
        court.setDescription(courtRequestDTO.getDescription());
        court.setLocation(courtRequestDTO.getLocation());
        court.setType(courtRequestDTO.getType());
        court.setPricePerHour(courtRequestDTO.getPricePerHour());

        Court courtSaved = courtRepository.save(court);

        CourtResponseDTO courtResponseDTO = new CourtResponseDTO();
        
        courtResponseDTO.setId(courtSaved.getId());
        courtResponseDTO.setName(courtSaved.getName());
        courtResponseDTO.setDescription(courtSaved.getDescription());
        courtResponseDTO.setLocation(courtSaved.getLocation());
        courtResponseDTO.setActive(courtSaved.isActive());
        courtResponseDTO.setCreatedAt(courtSaved.getCreatedAt());
        courtResponseDTO.setType(courtSaved.getType());
        courtResponseDTO.setPricePerHour(courtSaved.getPricePerHour());

        return courtResponseDTO;

    }

    @Override
    public void deleteCourt(Long id) {
        
        Court court = courtRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Cancha no encontrada con id: " + id)
        );

        courtRepository.delete(court);

    }

}
