package com.reservas.apidereservas.service.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.reservas.apidereservas.dto.payment.PaymentRequestDTO;
import com.reservas.apidereservas.dto.payment.PaymentResponseDTO;
import com.reservas.apidereservas.entity.Court;
import com.reservas.apidereservas.entity.Payment;
import com.reservas.apidereservas.entity.Reservation;
import com.reservas.apidereservas.entity.User;
import com.reservas.apidereservas.entity.enums.CourtType;
import com.reservas.apidereservas.entity.enums.PaymentMethod;
import com.reservas.apidereservas.entity.enums.PaymentStatus;
import com.reservas.apidereservas.entity.enums.ReservationStatus;
import com.reservas.apidereservas.entity.enums.Role;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.PaymentRepository;
import com.reservas.apidereservas.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock 
    private PaymentRepository paymentRepository;

    @Mock 
    private ReservationRepository reservationRepository;

    @InjectMocks 
    private PaymentServiceImpl paymentServiceImpl;

    @Test
    void shouldCreatePaymentSuccessfully() {

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
        reservation1.setEndTime(LocalTime.of(11, 30));
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(createdAt);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
        
        paymentRequestDTO.setReservationId(1L);
        paymentRequestDTO.setMethod(PaymentMethod.CARD);

        when(paymentRepository.existsByReservationId(1L))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setId(1L);
                    return payment;
                });

        PaymentResponseDTO result = paymentServiceImpl.createPayment(paymentRequestDTO);

        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("2250.00"), result.getAmount());
        assertEquals(PaymentMethod.CARD, result.getMethod());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertEquals(1L, result.getReservationId());
        assertNull(result.getPaymentDate());

    }

    @Test
    void shouldThrowExceptionWhenReservationDoesNotExist() {

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.empty());

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();

        paymentRequestDTO.setReservationId(1L);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> {
                paymentServiceImpl.createPayment(paymentRequestDTO);
            }
        );

        assertEquals("Reservación no encontrada con id: 1", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenReservationAlreadyHasPayment() {

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        when(paymentRepository.existsByReservationId(1L))
                .thenReturn(true);

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();

        paymentRequestDTO.setReservationId(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> {
                paymentServiceImpl.createPayment(paymentRequestDTO);
            }
        );

        assertEquals("La reserva ya tiene un pago asociado", exception.getMessage());

    }

    @Test
    void shouldCalculatePaymentAmountCorrectly() {

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
        court2.setPricePerHour(new BigDecimal("2000.00"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 9, 0);

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(14, 0));
        reservation1.setEndTime(LocalTime.of(14, 45));
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(createdAt);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();

        paymentRequestDTO.setReservationId(1L);
        paymentRequestDTO.setMethod(PaymentMethod.CARD);

        when(paymentRepository.existsByReservationId(1L))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setId(1L);
                    return payment;
                });

        PaymentResponseDTO result = paymentServiceImpl.createPayment(paymentRequestDTO);

        assertEquals(new BigDecimal("1500.00"), result.getAmount());

    }

    @Test
    void shouldSetPaymentDateAsNullWhenCreatingPayment() {

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
        court2.setPricePerHour(new BigDecimal("2000.00"));
        court2.setActive(true);
        court2.setCreatedAt(LocalDateTime.now());

        Reservation reservation1 = new Reservation();

        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 9, 0);

        reservation1.setId(1L);
        reservation1.setDate(LocalDate.of(2026, 10, 20));
        reservation1.setStartTime(LocalTime.of(14, 0));
        reservation1.setEndTime(LocalTime.of(14, 45));
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        reservation1.setUser(user1);
        reservation1.setCourt(court2);
        reservation1.setCreatedAt(createdAt);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();

        paymentRequestDTO.setReservationId(1L);
        paymentRequestDTO.setMethod(PaymentMethod.CARD);

        when(paymentRepository.existsByReservationId(1L))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setId(1L);
                    return payment;
                });

        PaymentResponseDTO result = paymentServiceImpl.createPayment(paymentRequestDTO);

        assertNull(result.getPaymentDate());
        assertEquals(PaymentStatus.PENDING, result.getStatus());

    }

    @Test 
    void shouldThrowExceptionWhenReservationIsCancelled() {

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);
        reservation1.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation1));

        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();

        paymentRequestDTO.setReservationId(1L);
        paymentRequestDTO.setMethod(PaymentMethod.CARD);

        when(paymentRepository.existsByReservationId(1L))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> {
                paymentServiceImpl.createPayment(paymentRequestDTO);
            }
        );

        assertEquals("No se puede crear un pago para una reserva cancelada", exception.getMessage());

    }

    @Test 
    void shouldGetPaymentByIdSuccessfully() {

        Reservation reservation = new Reservation();

        reservation.setId(1L);

        Payment payment = new Payment();

        payment.setId(1L);
        payment.setReservation(reservation);
        payment.setAmount(new BigDecimal("2250.00"));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setMethod(PaymentMethod.CARD);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentResponseDTO result = paymentServiceImpl.getPaymentById(1L);

        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getReservation().getId(), result.getReservationId());
        assertEquals(payment.getAmount(), result.getAmount());
        assertEquals(payment.getStatus(), result.getStatus());
        assertEquals(payment.getMethod(), result.getMethod());
    }

    @Test
    void shouldThrowExceptionWhenPaymentDoesNotExist() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> {
                paymentServiceImpl.getPaymentById(1L);
            }
        );

        assertEquals("Pago no encontrado con id: 1", exception.getMessage());

    }

    @Test 
    void shouldMapPaymentToResponseDTOCorrectly() {

        Reservation reservation = new Reservation();

        reservation.setId(1L);

        Payment payment = new Payment();

        payment.setId(1L);
        payment.setAmount(new BigDecimal("2250.00"));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setMethod(PaymentMethod.CARD);
        payment.setReservation(reservation);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentResponseDTO result = paymentServiceImpl.getPaymentById(1L);

        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getAmount(), result.getAmount());
        assertEquals(payment.getPaymentDate(), result.getPaymentDate());
        assertEquals(payment.getStatus(), result.getStatus());
        assertEquals(payment.getMethod(), result.getMethod());
        assertEquals(payment.getReservation().getId(), result.getReservationId());
    }

    @Test
    void shouldGetAllPaymentsSuccessfully() {

        Reservation reservation1 = new Reservation();

        reservation1.setId(1L);

        Reservation reservation2 = new Reservation();

        reservation2.setId(2L);

        Payment payment1 = new Payment();

        payment1.setId(11L);
        payment1.setAmount(new BigDecimal("1500"));
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setStatus(PaymentStatus.PENDING);
        payment1.setMethod(PaymentMethod.CARD);
        payment1.setReservation(reservation1);

        Payment payment2 = new Payment();

        payment2.setId(12L);
        payment2.setAmount(new BigDecimal("2250"));
        payment2.setPaymentDate(LocalDateTime.now());
        payment2.setStatus(PaymentStatus.COMPLETED);
        payment2.setMethod(PaymentMethod.TRANSFER);
        payment2.setReservation(reservation2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Payment> paymentPage = new PageImpl<>(
            List.of(payment1, payment2),
            pageable,
            2
        );

        when(paymentRepository.findAll(pageable))
                .thenReturn(paymentPage);

        Page<PaymentResponseDTO> result = paymentServiceImpl.getAllPayments(pageable);

        assertEquals(2, result.getTotalElements());
        
        assertEquals(payment1.getAmount(), result.getContent().get(0).getAmount());
        assertEquals(payment2.getAmount(), result.getContent().get(1).getAmount());
        
        assertEquals(payment1.getId(), result.getContent().get(0).getId());
        assertEquals(payment2.getId(), result.getContent().get(1).getId());
        
        assertEquals(reservation1.getId(), result.getContent().get(0).getReservationId());
        assertEquals(reservation2.getId(), result.getContent().get(1).getReservationId());
        
        assertEquals(payment1.getStatus(), result.getContent().get(0).getStatus());
        assertEquals(payment2.getStatus(), result.getContent().get(1).getStatus());

        assertEquals(payment1.getMethod(), result.getContent().get(0).getMethod());
        assertEquals(payment2.getMethod(), result.getContent().get(1).getMethod());

    }

    @Test 
    void shouldReturnEmptyPageWhenNoPaymentsExist() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Payment> paymentPage = new PageImpl<>(
            List.of(),
            pageable,
            0
        );

        when(paymentRepository.findAll(pageable))
                .thenReturn(paymentPage);

        Page<PaymentResponseDTO> result = paymentServiceImpl.getAllPayments(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

    }

    @Test 
    void shouldRespectPaginationParameters() {

        Pageable pageable = PageRequest.of(1, 5);

        Page<Payment> paymentPage = new PageImpl<>(
            List.of(),
            pageable,
            0
        );

        when(paymentRepository.findAll(pageable))
                .thenReturn(paymentPage);

        Page<PaymentResponseDTO> result = paymentServiceImpl.getAllPayments(pageable);

        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());

    }

}
