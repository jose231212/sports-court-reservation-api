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

import com.reservas.apidereservas.dto.payment.PaymentRequestDTO;
import com.reservas.apidereservas.dto.payment.PaymentResponseDTO;
import com.reservas.apidereservas.service.payment.PaymentService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @ApiResponses({@ApiResponse(responseCode = "201", description = "Payment created correctly")})
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {

        PaymentResponseDTO paymentResponseDTO = paymentService.createPayment(paymentRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDTO);
    }

    @ApiResponses({@ApiResponse(responseCode = "200", description = "Payment found correctly")})
    @GetMapping("/{id}")
    public PaymentResponseDTO getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @ApiResponses({@ApiResponse(responseCode = "200", description = "Payments found correctly")})
    @GetMapping 
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable){
        return paymentService.getAllPayments(pageable);
    }

    @ApiResponses({@ApiResponse(responseCode = "200", description = "Payment updated correctly")})
    @PutMapping("/{id}")
    public PaymentResponseDTO updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        return paymentService.updatePayment(id, paymentRequestDTO);
    }

    @ApiResponses({@ApiResponse(responseCode = "204", description = "Payment deleted correctly")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentById(@PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }

}
