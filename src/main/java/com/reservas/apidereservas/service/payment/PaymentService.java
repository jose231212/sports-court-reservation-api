package com.reservas.apidereservas.service.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.reservas.apidereservas.dto.payment.PaymentRequestDTO;
import com.reservas.apidereservas.dto.payment.PaymentResponseDTO;

public interface PaymentService {

    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO);

    PaymentResponseDTO getPaymentById(Long id);

    Page<PaymentResponseDTO> getAllPayments(Pageable pageable);

    PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO paymentRequestDTO);
    
    void deletePayment(Long id);

}
