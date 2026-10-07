package com.reservas.apidereservas.service.user;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.reservas.apidereservas.dto.user.UserRequestDTO;
import com.reservas.apidereservas.dto.user.UserResponseDTO;
import com.reservas.apidereservas.entity.User;
import com.reservas.apidereservas.entity.enums.Role;
import com.reservas.apidereservas.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();

        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setActive(savedUser.isActive());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setRole(savedUser.getRole());

        return  response;

    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }



}
