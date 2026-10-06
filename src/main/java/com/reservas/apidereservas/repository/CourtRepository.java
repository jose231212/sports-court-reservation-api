package com.reservas.apidereservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas.apidereservas.entity.Court;

public interface CourtRepository extends JpaRepository<Court, Long> {

}
