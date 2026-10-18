package com.reservas.apidereservas.dto.payment;

import com.reservas.apidereservas.entity.enums.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class PaymentRequestDTO {

    @Schema(
        description = "ID of the reservation associated with the payment",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Long reservationId;

    @Schema(
        description = "Payment method selected by the user",
        example = "CARD",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull 
    private PaymentMethod method;

}
