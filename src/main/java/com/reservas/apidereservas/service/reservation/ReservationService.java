package com.reservas.apidereservas.service.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.reservas.apidereservas.dto.reservation.ReservationRequestDTO;
import com.reservas.apidereservas.dto.reservation.ReservationResponseDTO;

public interface ReservationService {

    ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO);

    ReservationResponseDTO getReservationById(Long id);

    Page<ReservationResponseDTO> getAllReservations(Pageable pageable);

    ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO reservationRequestDTO);

    void deleteReservation(Long id);

}
