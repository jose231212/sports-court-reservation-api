package com.reservas.apidereservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas.apidereservas.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
