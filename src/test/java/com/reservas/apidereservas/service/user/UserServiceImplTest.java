package com.reservas.apidereservas.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservas.apidereservas.dto.user.UserRequestDTO;
import com.reservas.apidereservas.dto.user.UserResponseDTO;
import com.reservas.apidereservas.entity.User;
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

        verify(userRepository).save(any(User.class));

    }   

}
