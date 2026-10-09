package com.reservas.apidereservas.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        assertEquals("Usuario no encontrado con id: 999", exception.getMessage());

        verify(userRepository).findById(999L);

    }

    @Test
    void shouldGetAllUsersSuccessfully() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        User user2 = new User();

        user2.setId(2L);
        user2.setName("Aurelio Pereira");
        user2.setEmail("apereira@gmail.com");
        user2.setActive(true);
        user2.setRole(Role.USER);
        user2.setCreatedAt(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 2);

        Page<User> userPage = new PageImpl<>(
            List.of(user1, user2),
            pageable,
            2
        );

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        Page<UserResponseDTO> result = userServiceImpl.getAllUsers(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSize());

        assertEquals(user1.getId(), result.getContent().get(0).getId());
        assertEquals(user1.getName(), result.getContent().get(0).getName());
        assertEquals(user1.getEmail(), result.getContent().get(0).getEmail());
        assertTrue(result.getContent().get(0).isActive());
        assertEquals(user1.getRole(), result.getContent().get(0).getRole());
        assertEquals(user1.getCreatedAt(), result.getContent().get(0).getCreatedAt());

        assertEquals(user2.getId(), result.getContent().get(1).getId());
        assertEquals(user2.getName(), result.getContent().get(1).getName());
        assertEquals(user2.getEmail(), result.getContent().get(1).getEmail());
        assertTrue(result.getContent().get(1).isActive());
        assertEquals(user2.getRole(), result.getContent().get(1).getRole());
        assertEquals(user2.getCreatedAt(), result.getContent().get(1).getCreatedAt());

        verify(userRepository).findAll(pageable);


    }

    @Test
    void shouldUpdateUserSuccessfully(){

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Martinez");
        user.setEmail("pmartinez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserRequestDTO userRequestDTO = new UserRequestDTO();

        userRequestDTO.setName("Pedro Luciano");
        userRequestDTO.setEmail("pluciano@gmail.com");
        userRequestDTO.setPassword("123456");

        when(userRepository.save(any(User.class)))
                 .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userServiceImpl.updateUser(1L, userRequestDTO);

        assertEquals(user.getId(), result.getId());
        assertEquals(userRequestDTO.getName(), result.getName());
        assertEquals(userRequestDTO.getEmail(), result.getEmail());
        assertTrue(result.isActive());
        assertEquals(Role.USER, result.getRole());
        assertEquals(user.getCreatedAt(), result.getCreatedAt());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));

    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {

        UserRequestDTO userRequestDTO = new UserRequestDTO();

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userServiceImpl.updateUser(1L, userRequestDTO);
        });

        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void shouldDeleteUserSuccessfuly() {

        User user = new User();

        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userServiceImpl.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);

    }

    @Test 
    void shouldThrowExceptionWhenUserDoesNotExistOnDelete() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {

            userServiceImpl.deleteUser(1L);

        });

        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any(User.class));

    }

}
