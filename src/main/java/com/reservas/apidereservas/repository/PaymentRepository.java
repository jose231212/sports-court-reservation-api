package com.reservas.apidereservas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas.apidereservas.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

    boolean existsByReservationId(Long reservationId);

    Page<Payment> findByReservationUserId(Long userId, Pageable pageable);

}
