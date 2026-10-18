package com.reservas.apidereservas.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.reservas.apidereservas.entity.enums.PaymentMethod;
import com.reservas.apidereservas.entity.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class PaymentResponseDTO {

    @Schema(description = "Unique identifier of the payment", example = "1")
    private Long id;

    @Schema(description = "Calculated amount of the payment", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "Date and the time when the payment was recorded", example = "2026-10-18T14:30:00")
    private LocalDateTime paymentDate;

    @Schema(description = "Current status of the payment", example = "PENDING")
    private PaymentStatus status;

    @Schema(description = "Payment method used", example = "CARD")
    private PaymentMethod method;

    @Schema(description = "ID of the reservation associated with the payment", example = "1")
    private Long reservationId;

}
