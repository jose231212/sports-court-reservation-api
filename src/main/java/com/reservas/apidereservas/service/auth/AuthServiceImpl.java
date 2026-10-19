package com.reservas.apidereservas.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.reservas.apidereservas.dto.auth.AuthResponseDTO;
import com.reservas.apidereservas.dto.auth.LoginRequestDTO;
import com.reservas.apidereservas.service.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;

    private  final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long expiration;
    
    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

            authenticationManager.authenticate(authenticationToken);

            String token = jwtService.generateToken(loginRequestDTO.getEmail());

            AuthResponseDTO authResponseDTO = new AuthResponseDTO();
            authResponseDTO.setToken(token);
            authResponseDTO.setTokenType("Bearer");
            authResponseDTO.setExpiresIn(expiration);

            return authResponseDTO;

    }

}
