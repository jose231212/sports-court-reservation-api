package com.reservas.apidereservas.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.reservas.apidereservas.entity.enums.PaymentMethod;
import com.reservas.apidereservas.entity.enums.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class PaymentResponseDTO {

    private Long id;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private PaymentStatus status;

    private PaymentMethod method;

    private Long reservationId;

}
