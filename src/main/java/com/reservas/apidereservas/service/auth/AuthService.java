package com.reservas.apidereservas.service.auth;

import com.reservas.apidereservas.dto.auth.AuthResponseDTO;
import com.reservas.apidereservas.dto.auth.LoginRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);

}
