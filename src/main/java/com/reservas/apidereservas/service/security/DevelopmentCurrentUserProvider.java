package com.reservas.apidereservas.service.security;

import org.springframework.stereotype.Service;

import com.reservas.apidereservas.entity.User;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DevelopmentCurrentUserProvider implements CurrentUserProvider {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {

        Long id = 1L;

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        return user;

    }

}
