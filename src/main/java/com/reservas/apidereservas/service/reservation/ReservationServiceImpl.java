package com.reservas.apidereservas.service.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.reservas.apidereservas.dto.reservation.ReservationRequestDTO;
import com.reservas.apidereservas.dto.reservation.ReservationResponseDTO;
import com.reservas.apidereservas.entity.Court;
import com.reservas.apidereservas.entity.Reservation;
import com.reservas.apidereservas.entity.User;
import com.reservas.apidereservas.entity.enums.ReservationStatus;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.CourtRepository;
import com.reservas.apidereservas.repository.ReservationRepository;
import com.reservas.apidereservas.service.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final CourtRepository courtRepository;

    private final CurrentUserProvider currentUserProvider;

    @Override
    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO) {

        User user = currentUserProvider.getCurrentUser();

        Court court = courtRepository.findById(reservationRequestDTO.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cancha no encontrada con id: " + reservationRequestDTO.getCourtId()));

        if (!court.isActive()) {
            throw new IllegalArgumentException("No se puede realizar reservas en una cancha inactiva");
        }

        if (!reservationRequestDTO.getEndTime().isAfter(reservationRequestDTO.getStartTime())) {
            throw new IllegalArgumentException("La hora de finalizacion debe ser posterior a la hora de inicio");
        }

        if (reservationRequestDTO.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "No se pueden realizar reservas para fechas pasadas"
            );
        }

        if (reservationRequestDTO.getDate().isEqual(LocalDate.now())
                && !reservationRequestDTO.getStartTime().isAfter(LocalTime.now())) {
            throw new IllegalArgumentException(
                "La hora de inicio debe ser posterior a la hora actual"
            );
        }
        
        boolean hasOverlap = reservationRepository.existsOverlappingReservation(
        court.getId(),
        reservationRequestDTO.getDate(),
        reservationRequestDTO.getStartTime(),
        reservationRequestDTO.getEndTime(),
        ReservationStatus.CANCELLED
                );

        if (hasOverlap) {
            throw new IllegalArgumentException(
                "La cancha ya tiene una reserva en ese horario"
        );
        }

        Reservation reservation = new Reservation();

        reservation.setDate(reservationRequestDTO.getDate());
        reservation.setStartTime(reservationRequestDTO.getStartTime());
        reservation.setEndTime(reservationRequestDTO.getEndTime());
        reservation.setUser(user);
        reservation.setCourt(court);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation reservationSaved = reservationRepository.save(reservation);

        ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO();

        reservationResponseDTO.setId(reservationSaved.getId());
        reservationResponseDTO.setDate(reservationSaved.getDate());
        reservationResponseDTO.setStartTime(reservationSaved.getStartTime());
        reservationResponseDTO.setEndTime(reservationSaved.getEndTime());
        reservationResponseDTO.setStatus(reservationSaved.getStatus());
        reservationResponseDTO.setCreatedAt(reservationSaved.getCreatedAt());
        reservationResponseDTO.setUserId(reservationSaved.getUser().getId());
        reservationResponseDTO.setCourtId(reservationSaved.getCourt().getId());

        return reservationResponseDTO;

    }

    @Override
    public ReservationResponseDTO getReservationById(Long id) {
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservacion no encontrada con id: " + id));

        ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO();

        reservationResponseDTO.setId(reservation.getId());
        reservationResponseDTO.setDate(reservation.getDate());
        reservationResponseDTO.setStartTime(reservation.getStartTime());
        reservationResponseDTO.setEndTime(reservation.getEndTime());
        reservationResponseDTO.setStatus(reservation.getStatus());
        reservationResponseDTO.setCreatedAt(reservation.getCreatedAt());
        reservationResponseDTO.setUserId(reservation.getUser().getId());
        reservationResponseDTO.setCourtId(reservation.getCourt().getId());

        return reservationResponseDTO;

    }

    @Override
    public Page<ReservationResponseDTO> getAllReservations(Pageable pageable) {
        
        Page<Reservation> reservations = reservationRepository.findAll(pageable); 

        return reservations.map(reservation -> {
            ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO();

            reservationResponseDTO.setId(reservation.getId());
            reservationResponseDTO.setDate(reservation.getDate());
            reservationResponseDTO.setStartTime(reservation.getStartTime());
            reservationResponseDTO.setEndTime(reservation.getEndTime());
            reservationResponseDTO.setStatus(reservation.getStatus());
            reservationResponseDTO.setCreatedAt(reservation.getCreatedAt());
            reservationResponseDTO.setUserId(reservation.getUser().getId());
            reservationResponseDTO.setCourtId(reservation.getCourt().getId());

            return  reservationResponseDTO;
        });

    }

    @Override
    public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO reservationRequestDTO) {
        
        Reservation reservation = reservationRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reservacion no encontrada con id: " + id));

        Court court = courtRepository.findById(reservationRequestDTO.getCourtId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada con id: " + reservationRequestDTO.getCourtId()));

        if (!court.isActive()) {
            throw new IllegalArgumentException("No se puede realizar reservas en una cancha inactiva");
        }

        if (!reservationRequestDTO.getEndTime().isAfter(reservationRequestDTO.getStartTime())) {
            throw new IllegalArgumentException("La hora de finalizacion debe ser posterior a la hora de inicio");
        }

        if (reservationRequestDTO.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "No se pueden realizar reservas para fechas pasadas"
            );
        }

        if (reservationRequestDTO.getDate().isEqual(LocalDate.now())
                && !reservationRequestDTO.getStartTime().isAfter(LocalTime.now())) {
            throw new IllegalArgumentException(
                "La hora de inicio debe ser posterior a la hora actual"
            );
        }
        
        boolean hasOverlap = reservationRepository.existsOverlappingReservationExcludingReservation(
        court.getId(),
        reservationRequestDTO.getDate(),
        reservationRequestDTO.getStartTime(),
        reservationRequestDTO.getEndTime(),
        ReservationStatus.CANCELLED,
        id
        );

        if (hasOverlap) {
            throw new IllegalArgumentException(
                "La cancha ya tiene una reserva en ese horario"
            );
        }

        reservation.setDate(reservationRequestDTO.getDate());
        reservation.setStartTime(reservationRequestDTO.getStartTime());
        reservation.setEndTime(reservationRequestDTO.getEndTime());
        reservation.setCourt(court);

        Reservation reservationSave = reservationRepository.save(reservation);

        ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO();

        reservationResponseDTO.setId(reservationSave.getId());
        reservationResponseDTO.setDate(reservationSave.getDate());
        reservationResponseDTO.setStartTime(reservationSave.getStartTime());
        reservationResponseDTO.setEndTime(reservationSave.getEndTime());
        reservationResponseDTO.setStatus(reservationSave.getStatus());
        reservationResponseDTO.setCreatedAt(reservationSave.getCreatedAt());
        reservationResponseDTO.setUserId(reservationSave.getUser().getId());
        reservationResponseDTO.setCourtId(reservationSave.getCourt().getId());

        return reservationResponseDTO;

    }

    @Override
    public void deleteReservation(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteReservation'");
    }

}
