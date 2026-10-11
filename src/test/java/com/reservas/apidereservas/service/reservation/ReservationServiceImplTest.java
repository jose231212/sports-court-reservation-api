package com.reservas.apidereservas.service.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
