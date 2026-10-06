package com.reservas.apidereservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas.apidereservas.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
