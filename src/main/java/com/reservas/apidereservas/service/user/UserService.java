package com.reservas.apidereservas.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.reservas.apidereservas.dto.user.UserRequestDTO;
import com.reservas.apidereservas.dto.user.UserResponseDTO;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    UserResponseDTO getUserById(Long id);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);

    void deleteUser(Long id);

}
