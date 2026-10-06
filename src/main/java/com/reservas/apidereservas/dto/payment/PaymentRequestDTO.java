package com.reservas.apidereservas.dto.payment;

import com.reservas.apidereservas.entity.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class PaymentRequestDTO {

    @NotNull
    private Long reservationId;

    @NotNull 
    private PaymentMethod method;

}
