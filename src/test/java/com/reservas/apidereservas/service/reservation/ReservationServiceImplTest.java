package com.reservas.apidereservas.service.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.reservas.apidereservas.dto.reservation.ReservationRequestDTO;
import com.reservas.apidereservas.dto.reservation.ReservationResponseDTO;
import com.reservas.apidereservas.entity.Court;
import com.reservas.apidereservas.entity.Reservation;
import com.reservas.apidereservas.entity.User;
import com.reservas.apidereservas.entity.enums.CourtType;
import com.reservas.apidereservas.entity.enums.ReservationStatus;
import com.reservas.apidereservas.entity.enums.Role;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.CourtRepository;
import com.reservas.apidereservas.repository.ReservationRepository;
import com.reservas.apidereservas.service.court.CourtServiceImpl;
import com.reservas.apidereservas.service.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private CourtServiceImpl courtServiceImpl;

    @InjectMocks
    private ReservationServiceImpl reservationServiceImpl;

    @Test
    void shouldCreateReservationSuccessfully() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setDate(LocalDate.of(2026, 11, 11));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(11, 0));
        reservationRequestDTO.setCourtId(1L);

        when(reservationRepository.existsOverlappingReservation(reservationRequestDTO.getCourtId(),
                reservationRequestDTO.getDate(), reservationRequestDTO.getStartTime(),
                reservationRequestDTO.getEndTime(), ReservationStatus.CANCELLED))
                .thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation savedReservation = invocation.getArgument(0);
                    savedReservation.setId(11L);
                    return savedReservation;
                });

        ReservationResponseDTO result = reservationServiceImpl.createReservation(reservationRequestDTO);

        assertEquals(11L, result.getId());
        assertEquals(LocalDate.of(2026, 11, 11), result.getDate());
        assertEquals(ReservationStatus.PENDING, result.getStatus());
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getCourtId());

    }

    @Test
    void shouldThrowExceptionWhenCourtIsInactive() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(false);
        court.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court));

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setDate(LocalDate.of(2026, 11, 11));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(11, 0));
        reservationRequestDTO.setCourtId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationServiceImpl.createReservation(reservationRequestDTO));

        assertEquals("No se puede realizar reservas en una cancha inactiva", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsNotAfterStartTime() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court));

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setDate(LocalDate.of(2026, 12, 12));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(10, 0));
        reservationRequestDTO.setCourtId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationServiceImpl.createReservation(reservationRequestDTO));

        assertEquals("La hora de finalizacion debe ser posterior a la hora de inicio", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenReservationDateIsInThePast() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court));

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setDate(LocalDate.now().minusDays(1));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(11, 0));
        reservationRequestDTO.setCourtId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationServiceImpl.createReservation(reservationRequestDTO));

        assertEquals("No se pueden realizar reservas para fechas pasadas", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsNotAfterCurrentTimeForToday() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court));

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        LocalTime now = LocalTime.now();

        reservationRequestDTO.setDate(LocalDate.now());
        reservationRequestDTO.setStartTime(now.minusMinutes(10));
        reservationRequestDTO.setEndTime(now.minusMinutes(5));
        reservationRequestDTO.setCourtId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationServiceImpl.createReservation(reservationRequestDTO));

        assertEquals("La hora de inicio debe ser posterior a la hora actual", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenCourtHasOverlappingReservation() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court));

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setDate(LocalDate.now().plusDays(1));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(11, 0));
        reservationRequestDTO.setCourtId(1L);

        when(reservationRepository.existsOverlappingReservation(reservationRequestDTO.getCourtId(),
                reservationRequestDTO.getDate(), reservationRequestDTO.getStartTime(),
                reservationRequestDTO.getEndTime(), ReservationStatus.CANCELLED))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationServiceImpl.createReservation(reservationRequestDTO));

        assertEquals("La cancha ya tiene una reserva en ese horario", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenCourtDoesNotExist() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser())
        .thenReturn(user);

        when(courtRepository.findById(1L))
                .thenReturn(Optional.empty());

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();
        reservationRequestDTO.setCourtId(1L);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> {
                    reservationServiceImpl.createReservation(reservationRequestDTO);
                });

        assertEquals("Cancha no encontrada con id: 1", exception.getMessage());

    }

    @Test
    void shouldGetReservationByIdSuccessfully() {

        User user = new User();

        user.setId(1L);
        user.setName("Pedro Fernandez");
        user.setEmail("pfernandez@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        Court court = new Court();

        court.setId(1L);
        court.setName("Baloncesto");
        court.setDescription("Cancha para jugar baloncesto");
        court.setType(CourtType.BALONCESTO);
        court.setLocation("Lorica");
        court.setPricePerHour(new BigDecimal("1500"));
        court.setActive(true);
        court.setCreatedAt(LocalDateTime.now());

        Reservation reservation = new Reservation();

        reservation.setId(1L);
        reservation.setDate(LocalDate.of(2026, 12, 12));
        reservation.setStartTime(LocalTime.of(10, 0));
        reservation.setEndTime(LocalTime.of(11, 0));
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setUser(user);
        reservation.setCourt(court);
        reservation.setCreatedAt(LocalDateTime.now());

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        ReservationResponseDTO result = reservationServiceImpl.getReservationById(1L);

        assertEquals(reservation.getId(), result.getId());
        assertEquals(reservation.getDate(), result.getDate());
        assertEquals(reservation.getStartTime(), result.getStartTime());
        assertEquals(reservation.getEndTime(), result.getEndTime());
        assertEquals(reservation.getStatus(), result.getStatus());
        assertEquals(reservation.getUser().getId(), result.getUserId());
        assertEquals(reservation.getCourt().getId(), result.getCourtId());
        assertEquals(reservation.getCreatedAt(), result.getCreatedAt());

    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExist() {

            when(reservationRepository.findById(1L))
                            .thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                            () -> {
                                    reservationServiceImpl.getReservationById(1L);
                            });

            assertEquals("Reservacion no encontrada con id: 1", exception.getMessage());

    }

    @Test 
    void shouldGetAllReservationSuccessfully() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        User user2 = new User();

        user2.setId(2L);
        user2.setName("Armando Fernandez");
        user2.setEmail("afernandez@gmail.com");
        user2.setActive(true);
        user2.setRole(Role.USER);
        user2.setCreatedAt(LocalDateTime.now());

        User admin = new User();

        admin.setId(3L);
        admin.setName("Administrador");
        admin.setEmail("admin@gmail.com");
        admin.setActive(true);
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        Court court1 = new Court();

        court1.setId(1L);
        court1.setName("Baloncesto");
        court1.setDescription("Cancha para jugar baloncesto");
        court1.setType(CourtType.BALONCESTO);
        court1.setLocation("Lorica");
        court1.setPricePerHour(new BigDecimal("1500"));
        court1.setActive(true);
        court1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(1L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 12, 12));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(LocalDateTime.now());

        Reservation reservation2 = new Reservation();

        reservation2.setId(2L);
        reservation2.setDate(LocalDate.of(2026, 12, 12));
        reservation2.setStartTime(LocalTime.of(8, 0));
        reservation2.setEndTime(LocalTime.of(9, 0));
        reservation2.setStatus(ReservationStatus.PENDING);
        reservation2.setUser(user2);
        reservation2.setCourt(court2);
        reservation2.setCreatedAt(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 2);

        Page<Reservation> reservationPage = new PageImpl<>(
                List.of(reservation1, reservation2),
                pageable,
                2
        );

        when(reservationRepository.findAll(pageable))
                .thenReturn(reservationPage);
        
        Page<ReservationResponseDTO> result = reservationServiceImpl.getAllReservations(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSize());
        assertEquals(2, result.getContent().size());

        assertEquals(reservation1.getId(), result.getContent().get(0).getId());
        assertEquals(reservation1.getDate(), result.getContent().get(0).getDate());
        assertEquals(reservation1.getStartTime(), result.getContent().get(0).getStartTime());
        assertEquals(reservation1.getEndTime(), result.getContent().get(0).getEndTime());
        assertEquals(reservation1.getStatus(), result.getContent().get(0).getStatus());
        assertEquals(reservation1.getUser().getId(), result.getContent().get(0).getUserId());
        assertEquals(reservation1.getCourt().getId(), result.getContent().get(0).getCourtId());
        assertEquals(reservation1.getCreatedAt(), result.getContent().get(0).getCreatedAt());

        assertEquals(reservation2.getId(), result.getContent().get(1).getId());
        assertEquals(reservation2.getDate(), result.getContent().get(1).getDate());
        assertEquals(reservation2.getStartTime(), result.getContent().get(1).getStartTime());
        assertEquals(reservation2.getEndTime(), result.getContent().get(1).getEndTime());
        assertEquals(reservation2.getStatus(), result.getContent().get(1).getStatus());
        assertEquals(reservation2.getUser().getId(), result.getContent().get(1).getUserId());
        assertEquals(reservation2.getCourt().getId(), result.getContent().get(1).getCourtId());
        assertEquals(reservation2.getCreatedAt(), result.getContent().get(1).getCreatedAt());

    }

    @Test 
    void shouldReturnCorrectPageWhenMoreReservationsExist() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court1 = new Court();

        court1.setId(1L);
        court1.setName("Baloncesto");
        court1.setDescription("Cancha para jugar baloncesto");
        court1.setType(CourtType.BALONCESTO);
        court1.setLocation("Lorica");
        court1.setPricePerHour(new BigDecimal("1500"));
        court1.setActive(true);
        court1.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 12, 12));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court1);
        reservation1.setCreatedAt(LocalDateTime.now());

        Reservation reservation2 = new Reservation();

        reservation2.setId(2L);
        reservation2.setDate(LocalDate.of(2026, 12, 12));
        reservation2.setStartTime(LocalTime.of(8, 0));
        reservation2.setEndTime(LocalTime.of(9, 0));
        reservation2.setStatus(ReservationStatus.PENDING);
        reservation2.setUser(user1);
        reservation2.setCourt(court1);
        reservation2.setCreatedAt(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 2);

        Page<Reservation> reservationPage = new PageImpl<>(
                List.of(reservation1, reservation2),
                pageable,
                5
        );

        User admin = new User();

        admin.setId(3L);
        admin.setName("Administrador");
        admin.setEmail("admin@gmail.com");
        admin.setActive(true);
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(reservationRepository.findAll(pageable))
                .thenReturn(reservationPage);

        Page<ReservationResponseDTO> result = reservationServiceImpl.getAllReservations(pageable);

        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getSize());
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());

    }

    @Test 
    void shouldUpdateReservationSuccessfully() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court1 = new Court();

        court1.setId(1L);
        court1.setName("Baloncesto");
        court1.setDescription("Cancha para jugar baloncesto");
        court1.setType(CourtType.BALONCESTO);
        court1.setLocation("Lorica");
        court1.setPricePerHour(new BigDecimal("1500"));
        court1.setActive(true);
        court1.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court1);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(courtRepository.findById(1L))
                .thenReturn(Optional.of(court1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(1L);
        reservationRequestDTO.setDate(LocalDate.of(2026, 11, 21));
        reservationRequestDTO.setStartTime(LocalTime.of(14, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(15, 0));
        
        when(reservationRepository.existsOverlappingReservationExcludingReservation(reservationRequestDTO.getCourtId(),
                        reservationRequestDTO.getDate(), reservationRequestDTO.getStartTime(),
                        reservationRequestDTO.getEndTime(), ReservationStatus.CANCELLED, 1L
                )).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponseDTO result = reservationServiceImpl.updateReservation(1L, reservationRequestDTO);

        assertEquals(1L, result.getId());
        assertEquals(reservationRequestDTO.getCourtId(), result.getCourtId());
        assertEquals(reservationRequestDTO.getDate(), result.getDate());
        assertEquals(reservationRequestDTO.getStartTime(), result.getStartTime());
        assertEquals(reservationRequestDTO.getEndTime(), result.getEndTime());
        assertEquals(reservation1.getStatus(), result.getStatus());
        assertEquals(reservation1.getUser().getId(), result.getUserId());
        assertEquals(reservation1.getCreatedAt(), result.getCreatedAt());

        verify(reservationRepository).save(any(Reservation.class));
        

    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExistWhenUpdating() {

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> {
                        reservationServiceImpl.updateReservation(1L, reservationRequestDTO);
                }
        );

        assertEquals("Reservacion no encontrada con id: 1", exception.getMessage());


    }

    @Test 
    void shouldThrowExceptionWhenCourtDoesNotExistWhenUpdatingReservation() {
        
        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court1 = new Court();

        court1.setId(1L);
        court1.setName("Baloncesto");
        court1.setDescription("Cancha para jugar baloncesto");
        court1.setType(CourtType.BALONCESTO);
        court1.setLocation("Lorica");
        court1.setPricePerHour(new BigDecimal("1500"));
        court1.setActive(true);
        court1.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court1);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(courtRepository.findById(2L))
                .thenReturn(Optional.empty());

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(2L);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> {
                        reservationServiceImpl.updateReservation(1L, reservationRequestDTO);
                }
        );

        assertEquals("Cancha no encontrada con id: 2", exception.getMessage());


    }

    @Test 
    void shouldThrowExceptionWhenCourtIsInactiveWhenUpdatingReservation() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(false);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(courtRepository.findById(2L))
                .thenReturn(Optional.of(court2));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(2L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> {
                        reservationServiceImpl.updateReservation(1L, reservationRequestDTO);
                }
        );

        assertEquals("No se puede realizar reservas en una cancha inactiva", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsNotAfterStartTimeWhenUpdatingReservation() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(courtRepository.findById(2L))
                .thenReturn(Optional.of(court2));

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(2L);
        reservationRequestDTO.setStartTime(LocalTime.of(15, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(14, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> {
                        reservationServiceImpl.updateReservation(1L, reservationRequestDTO);
                }
        );

        assertEquals("La hora de finalizacion debe ser posterior a la hora de inicio", exception.getMessage()); 

    }

    @Test 
    void shouldThrowExceptionWhenReservationDateIsInThePastWhenUpdatingReservation() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(courtRepository.findById(2L))
                .thenReturn(Optional.of(court2));

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);
        
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(2L);
        reservationRequestDTO.setDate(LocalDate.now().minusDays(1));
        reservationRequestDTO.setStartTime(LocalTime.of(15, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(16, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> {
                         reservationServiceImpl.updateReservation(1L, reservationRequestDTO);
                }
        );

        assertEquals("No se pueden realizar reservas para fechas pasadas", exception.getMessage());

    }

    @Test 
    void shouldThrowExceptionWhenStartTimeIsNotAfterCurrentTimeWhenUpdatingReservation() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(courtRepository.findById(2L))
                .thenReturn(Optional.of(court2));

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        LocalTime now = LocalTime.now();

        reservationRequestDTO.setCourtId(2L);
        reservationRequestDTO.setDate(LocalDate.now());
        reservationRequestDTO.setStartTime(now.minusHours(1));
        reservationRequestDTO.setEndTime(now.plusHours(1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> {
                        reservationServiceImpl.updateReservation(1L, reservationRequestDTO);
                }
        );

        assertEquals("La hora de inicio debe ser posterior a la hora actual", exception.getMessage());

    }

    @Test 
    void shouldThrowExceptionWhenCourtHasOverlappingReservationWhenUpdating() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(courtRepository.findById(2L))
                .thenReturn(Optional.of(court2));

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(2L);
        reservationRequestDTO.setDate(LocalDate.of(2026, 12, 12));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(11, 0));

        when(reservationRepository.existsOverlappingReservationExcludingReservation(
                reservationRequestDTO.getCourtId(),
                reservationRequestDTO.getDate(),
                reservationRequestDTO.getStartTime(), 
                reservationRequestDTO.getEndTime(), 
                ReservationStatus.CANCELLED, 
                1L
        )).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> {
                        reservationServiceImpl.updateReservation(1L, reservationRequestDTO);

                }
        );
        
        assertEquals("La cancha ya tiene una reserva en ese horario", exception.getMessage());

    }

    @Test 
    void shouldAllowUpdatingReservationWithoutConflictingWithItself() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(LocalDateTime.now());

        when(courtRepository.findById(2L))
                .thenReturn(Optional.of(court2));

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(2L);
        reservationRequestDTO.setDate(LocalDate.of(2026, 12, 12));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(11, 0));

        when(reservationRepository.existsOverlappingReservationExcludingReservation(
                reservationRequestDTO.getCourtId(), 
                reservationRequestDTO.getDate(), 
                reservationRequestDTO.getStartTime(), 
                reservationRequestDTO.getEndTime(), 
                ReservationStatus.CANCELLED, 
 1L
        )).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponseDTO result = reservationServiceImpl.updateReservation(1L, reservationRequestDTO);

        assertEquals(1L, result.getId());
        assertEquals(reservationRequestDTO.getDate(), result.getDate());
        assertEquals(reservationRequestDTO.getStartTime(), result.getStartTime());
        assertEquals(reservationRequestDTO.getEndTime(), result.getEndTime());
        assertEquals(ReservationStatus.PENDING, result.getStatus());

        verify(reservationRepository).save(any(Reservation.class));

    }

    @Test 
    void shouldPreserveUserStatusAndCreatedAtWhenUpdatingReservation() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 9, 0);

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(createdAt);

        when(courtRepository.findById(2L))
                .thenReturn(Optional.of(court2));

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();

        reservationRequestDTO.setCourtId(2L);
        reservationRequestDTO.setDate(LocalDate.of(2026, 12, 12));
        reservationRequestDTO.setStartTime(LocalTime.of(10, 0));
        reservationRequestDTO.setEndTime(LocalTime.of(11, 0));
        
        when(reservationRepository.existsOverlappingReservationExcludingReservation(
                reservationRequestDTO.getCourtId(), 
                reservationRequestDTO.getDate(), 
                reservationRequestDTO.getStartTime(), 
                reservationRequestDTO.getEndTime(), 
                ReservationStatus.CANCELLED, 
                1L
        )).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponseDTO result = reservationServiceImpl.updateReservation(1L, reservationRequestDTO);

        assertEquals(1L, result.getUserId());
        assertEquals(ReservationStatus.CONFIRMED, result.getStatus());
        assertEquals(createdAt, result.getCreatedAt());

    }

    @Test
    void shouldDeleteReservationSuccessfully() {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("Pedro Fernandez");
        user1.setEmail("pfernandez@gmail.com");
        user1.setActive(true);
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now());

        Court court2 = new Court();

        court2.setId(2L);
        court2.setName("Baloncesto");
        court2.setDescription("Cancha para jugar baloncesto");
        court2.setType(CourtType.BALONCESTO);
        court2.setLocation("Lorica");
        court2.setPricePerHour(new BigDecimal("1500"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 9, 0);

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(11, 0));
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(createdAt);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user1);

        reservationServiceImpl.deleteReservation(1L);

        verify(reservationRepository).delete(reservation1);

    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExistWhenDeleting() {

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> {
                        reservationServiceImpl.deleteReservation(1L);
                }
        );

        assertEquals("Reservacion no encontrada con id: 1", exception.getMessage());

    }

}
