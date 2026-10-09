package com.reservas.apidereservas.service.court;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.reservas.apidereservas.dto.court.CourtRequestDTO;
import com.reservas.apidereservas.dto.court.CourtResponseDTO;
import com.reservas.apidereservas.entity.Court;
import com.reservas.apidereservas.entity.enums.CourtType;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.CourtRepository;

@ExtendWith(MockitoExtension.class)
public class CourtServiceImplTest {

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks 
    private CourtServiceImpl courtServiceImpl;

    @Test 
    void shouldCreateCourtSuccessfully(){

        CourtRequestDTO courtRequestDTO = new CourtRequestDTO();

        courtRequestDTO.setName("Baloncesto");
        courtRequestDTO.setType(CourtType.BALONCESTO);
        courtRequestDTO.setDescription("Espacio para jugar baloncesto");
        courtRequestDTO.setLocation("Altamira");
        courtRequestDTO.setPricePerHour(new BigDecimal("1500"));

        when(courtRepository.save(any(Court.class)))
                    .thenAnswer(invocation -> {
                        Court court = invocation.getArgument(0);
                        court.setId(1L);
                        return court;
                    });

        CourtResponseDTO courtResponseDTO = courtServiceImpl.createCourt(courtRequestDTO);
        
        assertEquals(1L, courtResponseDTO.getId());
        assertEquals(courtRequestDTO.getName(), courtResponseDTO.getName());
        assertEquals(courtRequestDTO.getType(), courtResponseDTO.getType());
        assertEquals(courtRequestDTO.getDescription(), courtResponseDTO.getDescription());
        assertEquals(courtRequestDTO.getLocation(), courtResponseDTO.getLocation());
        assertEquals(courtRequestDTO.getPricePerHour(), courtResponseDTO.getPricePerHour());
        assertTrue(courtResponseDTO.isActive());
        assertNotNull(courtResponseDTO.getCreatedAt());

        verify(courtRepository).save(any(Court.class));

    }

    @Test
    void shouldGetCourtByIdSuccessfully() {

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());
        
        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court));

        CourtResponseDTO courtResponseDTO = courtServiceImpl.getCourtById(court.getId());

        assertEquals(court.getId(), courtResponseDTO.getId());
        assertEquals(court.getName(), courtResponseDTO.getName());
        assertEquals(court.getDescription(), courtResponseDTO.getDescription());
        assertEquals(court.getType(), courtResponseDTO.getType());
        assertEquals(court.getLocation(), courtResponseDTO.getLocation());
        assertEquals(court.getPricePerHour(), courtResponseDTO.getPricePerHour());
        assertTrue(courtResponseDTO.isActive());
        assertNotNull(courtResponseDTO.getCreatedAt());

        verify(courtRepository).findById(court.getId());

    }

    @Test
    void shouldThrowExceptionWhenCourtDoesNotExist() {

        when(courtRepository.findById(999L))
                .thenReturn(Optional.empty());
                
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            courtServiceImpl.getCourtById(999L);
        });

        assertEquals("Cancha no encontrada con id: 999", exception.getMessage());

        verify(courtRepository).findById(999L);

    }

    @Test
    void shouldGetAllCourtsSuccessfully() {

        Court court1 = new Court();

        court1.setId(1L);
        court1.setName("Baloncesto");
        court1.setDescription("Cancha para jugar baloncesto");
        court1.setType(CourtType.BALONCESTO);
        court1.setLocation("Monteria");
        court1.setPricePerHour(new BigDecimal("2000"));
        court1.setActive(true);
        court1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court1.setId(2L);
        court2.setName("Tenis");
        court2.setDescription("Cancha para jugar tenis");
        court2.setType(CourtType.TENIS);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 2);

        Page<Court> courtPage = new PageImpl<>(
            List.of(court1, court2),
            pageable,
            2
        );

        when(courtRepository.findAll(pageable))
                .thenReturn(courtPage);

        Page<CourtResponseDTO> result = courtServiceImpl.getAllCourts(pageable);


        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSize());
        assertEquals(2, result.getContent().size());

        assertEquals(court1.getId(), result.getContent().get(0).getId());
        assertEquals(court1.getName(), result.getContent().get(0).getName());
        assertEquals(court1.getDescription(), result.getContent().get(0).getDescription());
        assertEquals(court1.getType(), result.getContent().get(0).getType());
        assertEquals(court1.getLocation(), result.getContent().get(0).getLocation());
        assertEquals(court1.getPricePerHour(), result.getContent().get(0).getPricePerHour());
        assertTrue(result.getContent().get(0).isActive());
        assertEquals(court1.getCreatedAt(), result.getContent().get(0).getCreatedAt());

        assertEquals(court2.getId(), result.getContent().get(1).getId());
        assertEquals(court2.getName(), result.getContent().get(1).getName());
        assertEquals(court2.getDescription(), result.getContent().get(1).getDescription());
        assertEquals(court2.getType(), result.getContent().get(1).getType());
        assertEquals(court2.getLocation(), result.getContent().get(1).getLocation());
        assertEquals(court2.getPricePerHour(), result.getContent().get(1).getPricePerHour());
        assertTrue(result.getContent().get(1).isActive());
        assertEquals(court2.getCreatedAt(), result.getContent().get(1).getCreatedAt());

        verify(courtRepository).findAll(pageable);

    }

    @Test 
    void shouldUpdateCourtSuccessfully() {

        Court court1 = new Court();

        court1.setId(1L);
        court1.setName("Baloncesto");
        court1.setDescription("Cancha para jugar baloncesto");
        court1.setType(CourtType.BALONCESTO);
        court1.setLocation("Monteria");
        court1.setPricePerHour(new BigDecimal("2000"));
        court1.setActive(false);
        court1.setCreatedAt(LocalDateTime.now());

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court1));
            
        CourtRequestDTO courtRequestDTO = new CourtRequestDTO();

        courtRequestDTO.setName("Tenis");
        courtRequestDTO.setDescription("Cancha para jugar tenis");
        courtRequestDTO.setType(CourtType.TENIS);
        courtRequestDTO.setLocation("Lorica");
        courtRequestDTO.setPricePerHour(new BigDecimal("1500"));

        when(courtRepository.save(any(Court.class)))
                .thenAnswer(invoncation -> invoncation.getArgument(0));

        CourtResponseDTO result = courtServiceImpl.updateCourt(1L, courtRequestDTO);
        
        assertEquals(1L, result.getId());
        assertEquals(courtRequestDTO.getName(), result.getName());
        assertEquals(courtRequestDTO.getDescription(), result.getDescription());
        assertEquals(courtRequestDTO.getType(), result.getType());
        assertEquals(courtRequestDTO.getLocation(), result.getLocation());
        assertEquals(courtRequestDTO.getPricePerHour(), result.getPricePerHour());
        assertFalse(result.isActive());
        assertEquals(court1.getCreatedAt(), result.getCreatedAt());
        
        verify(courtRepository).findById(1L);
        verify(courtRepository).save(any(Court.class));

    }

    @Test 
    void shouldThrowExceptionWhenUpdatingNonExistingCourt() {

        when(courtRepository.findById(999L))
                    .thenReturn(Optional.empty());

        CourtRequestDTO courtRequestDTO = new CourtRequestDTO();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            courtServiceImpl.updateCourt(999L, courtRequestDTO);
        });

        assertEquals("Cancha no encontrada con id: 999", exception.getMessage());

        verify(courtRepository).findById(999L);
        verify(courtRepository, never()).save(any(Court.class));

    }

    @Test 
    void shouldDeleteCourtSuccessfully() {

        Court court1 = new Court();

        court1.setId(1L);
        court1.setName("Baloncesto");
        court1.setDescription("Cancha para jugar baloncesto");
        court1.setType(CourtType.BALONCESTO);
        court1.setLocation("Monteria");
        court1.setPricePerHour(new BigDecimal("2000"));
        court1.setActive(false);
        court1.setCreatedAt(LocalDateTime.now());

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court1));

        courtServiceImpl.deleteCourt(1L);

        verify(courtRepository).findById(1L);
        verify(courtRepository).delete(court1);

    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCourt() {

        when(courtRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            courtServiceImpl.deleteCourt(999L);
        });

        assertEquals("Cancha no encontrada con id: 999", exception.getMessage());

        verify(courtRepository).findById(999L);
        verify(courtRepository, never()).delete(any(Court.class));
                    

    }

}
