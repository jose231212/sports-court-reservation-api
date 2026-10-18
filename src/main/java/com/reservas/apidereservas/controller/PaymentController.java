package com.reservas.apidereservas.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.apidereservas.dto.ErrorResponseDTO;
import com.reservas.apidereservas.dto.payment.PaymentRequestDTO;
import com.reservas.apidereservas.dto.payment.PaymentResponseDTO;
import com.reservas.apidereservas.service.payment.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(
    name = "Payments", description = "Endpoints for creating, retrieving, updating, and deleting payments"
)
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Create payment", description = "Creates a new payment using the provided payment details and the associated reservation")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or payment rules violated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "Reservation not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        )
    })
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {

        PaymentResponseDTO paymentResponseDTO = paymentService.createPayment(paymentRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDTO);
    }

    @Operation(summary = "Get payment by ID", description = "Retrieves a payment using its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        )
    })
    @GetMapping("/{id}")
    public PaymentResponseDTO getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @Operation(summary = "Retrieve all payments", description = "Retrieves all payments using paginated results")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        )
    })
    @GetMapping 
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable){
        return paymentService.getAllPayments(pageable);
    }

    @Operation(summary = "Update a payment", description = "Updates an existing payment using its unique identifier and the provided payment details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or payment rules violated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "Payment or associated reservation not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        )
    })
    @PutMapping("/{id}")
    public PaymentResponseDTO updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        return paymentService.updatePayment(id, paymentRequestDTO);
    }

    @Operation(summary = "Delete payment by ID", description = "Deletes an existing payment using its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
        )

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentById(@PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }

}
