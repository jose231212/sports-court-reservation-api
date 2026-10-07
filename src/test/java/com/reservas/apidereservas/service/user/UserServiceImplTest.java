package com.reservas.apidereservas.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas.apidereservas.dto.user.UserRequestDTO;
import com.reservas.apidereservas.dto.user.UserResponseDTO;
import com.reservas.apidereservas.entity.User;
import com.reservas.apidereservas.entity.enums.Role;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks 
    private UserServiceImpl userServiceImpl;

    @Test
    void shouldCreateUserSuccessfully() {

        UserRequestDTO userRequestDTO = new UserRequestDTO();

        userRequestDTO.setName("Pedro Fernandez");
        userRequestDTO.setPassword("123456");
        userRequestDTO.setEmail("pfernandez@gmail.com");
        
        when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        user.setId(1L);
                        return  user;
                    });

        UserResponseDTO userResponseDTO = userServiceImpl.createUser(userRequestDTO);

        assertEquals(1L, userResponseDTO.getId());
        assertEquals("Pedro Fernandez", userResponseDTO.getName());
        assertEquals("pfernandez@gmail.com", userResponseDTO.getEmail());
        assertTrue(userResponseDTO.isActive());
        assertEquals(Role.USER, userResponseDTO.getRole());
        assertNotNull(userResponseDTO.getCreatedAt());

        verify(userRepository).save(any(User.class));

    }
    
    @Test
    void shouldGetUserByIdSuccessfully() {

        Long id = 1L;

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
                
        UserResponseDTO userResponseDTO = userServiceImpl.getUserById(id);

        assertEquals(1L, userResponseDTO.getId());
        assertEquals("Pedro Fernandez", userResponseDTO.getName());
        assertEquals("pfernandez@gmail.com", userResponseDTO.getEmail());
        assertTrue(userResponseDTO.isActive());
        assertEquals(Role.USER, userResponseDTO.getRole());
        assertNotNull(userResponseDTO.getCreatedAt());

        verify(userRepository).findById(id);

    }

    @Test 
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            
            userServiceImpl.getUserById(999L);
            
        });

        assertEquals("Usuario no encontrado con id: 999L", exception.getMessage());

        verify(userRepository).findById(999L);

    }

}
