package com.reservas.apidereservas.service.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.reservas.apidereservas.dto.payment.PaymentRequestDTO;
import com.reservas.apidereservas.dto.payment.PaymentResponseDTO;
import com.reservas.apidereservas.entity.Payment;
import com.reservas.apidereservas.entity.Reservation;
import com.reservas.apidereservas.entity.User;
import com.reservas.apidereservas.entity.enums.PaymentStatus;
import com.reservas.apidereservas.entity.enums.ReservationStatus;
import com.reservas.apidereservas.entity.enums.Role;
import com.reservas.apidereservas.exception.ResourceNotFoundException;
import com.reservas.apidereservas.repository.PaymentRepository;
import com.reservas.apidereservas.repository.ReservationRepository;
import com.reservas.apidereservas.service.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final ReservationRepository reservationRepository;

    private final CurrentUserProvider currentUserProvider;

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) {

        Reservation reservation = reservationRepository.findById(
                paymentRequestDTO.getReservationId()).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Reservación no encontrada con id: "
                                        + paymentRequestDTO.getReservationId()));

        if (paymentRepository.existsByReservationId(reservation.getId())) {
            throw new IllegalArgumentException(
                    "La reserva ya tiene un pago asociado");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "No se puede crear un pago para una reserva cancelada");
        }

        Duration duration = Duration.between(reservation.getStartTime(), reservation.getEndTime());

        BigDecimal pricePerHour = reservation.getCourt().getPricePerHour();

        BigDecimal minutes = BigDecimal.valueOf(duration.toMinutes());

        BigDecimal result = minutes.multiply(pricePerHour)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        Payment payment = new Payment();

        payment.setAmount(result);
        payment.setMethod(paymentRequestDTO.getMethod());
        payment.setReservation(reservation);
        payment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();

        paymentResponseDTO.setId(savedPayment.getId());
        paymentResponseDTO.setAmount(savedPayment.getAmount());
        paymentResponseDTO.setMethod(savedPayment.getMethod());
        paymentResponseDTO.setStatus(savedPayment.getStatus());
        paymentResponseDTO.setReservationId(savedPayment.getReservation().getId());
        paymentResponseDTO.setPaymentDate(savedPayment.getPaymentDate());

        return paymentResponseDTO;

    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        
        Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        User currentUser = currentUserProvider.getCurrentUser();

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = payment.getReservation().getUser().getId()
                .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AuthorizationDeniedException(
                "No tienes permisos para realizar esta consulta"
            );
        }

        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();

        paymentResponseDTO.setId(payment.getId());
        paymentResponseDTO.setAmount(payment.getAmount());
        paymentResponseDTO.setPaymentDate(payment.getPaymentDate());
        paymentResponseDTO.setStatus(payment.getStatus());
        paymentResponseDTO.setMethod(payment.getMethod());
        paymentResponseDTO.setReservationId(payment.getReservation().getId());

        return paymentResponseDTO;

    }

    @Override
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        
        User currentUser = currentUserProvider.getCurrentUser();

        Page<Payment> payments;

        if (currentUser.getRole() == Role.ADMIN) {
            payments = paymentRepository.findAll(pageable);
        }
        else{
            payments = paymentRepository.findByReservationUserId(currentUser.getId(), pageable);
        }

        return payments.map(payment -> {
            PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();

            paymentResponseDTO.setId(payment.getId());
            paymentResponseDTO.setAmount(payment.getAmount());
            paymentResponseDTO.setPaymentDate(payment.getPaymentDate());
            paymentResponseDTO.setStatus(payment.getStatus());
            paymentResponseDTO.setMethod(payment.getMethod());
            paymentResponseDTO.setReservationId(payment.getReservation().getId());

            return paymentResponseDTO;
        });

    }

    @Override
    public PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO paymentRequestDTO) {
        
        Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        User currentUser = currentUserProvider.getCurrentUser();

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = payment.getReservation().getUser().getId()
                .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AuthorizationDeniedException(
                "No tienes permisos para modificar este pago"
            );
        }

        payment.setMethod(paymentRequestDTO.getMethod());

        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();

        paymentResponseDTO.setId(savedPayment.getId());
        paymentResponseDTO.setAmount(savedPayment.getAmount());
        paymentResponseDTO.setPaymentDate(savedPayment.getPaymentDate());
        paymentResponseDTO.setStatus(savedPayment.getStatus());
        paymentResponseDTO.setMethod(savedPayment.getMethod());
        paymentResponseDTO.setReservationId(savedPayment.getReservation().getId());

        return paymentResponseDTO;

    }

    @Override
    public void deletePayment(Long id) {
        
        Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        User currentUser = currentUserProvider.getCurrentUser();

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = payment.getReservation().getUser().getId()
                .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AuthorizationDeniedException(
                "No tienes permisos para modificar este pago"
            );
        }

        paymentRepository.deleteById(payment.getId());

    }

}
