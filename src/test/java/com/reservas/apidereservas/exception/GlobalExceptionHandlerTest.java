package com.reservas.apidereservas.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.reservas.apidereservas.dto.ErrorResponseDTO;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleResourceNotFoundException() {

        ResourceNotFoundException exception = new ResourceNotFoundException("Cancha no encontrada con id: 99");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.exceptionHandler(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Cancha no encontrada con id: 99", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());

    }

    @Test
    void shouldHandleMethodArgumentNotValidException() throws NoSuchMethodException {

        Object target = new Object();

        BindingResult bindingResult = new BeanPropertyBindingResult(
                target, "requestDTO");

        bindingResult.addError(new FieldError("requestDTO", "email", "El correo no es valido"));

        bindingResult.addError(new FieldError("requestDTO", "name", "El nombre es obligatorio"));

        MethodParameter methodParameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.argumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals(400, response.getBody().getStatus());
        assertEquals("Error de validacion en los datos enviados", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());

        Map<String, String> errors = response.getBody().getErrors();

        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("El correo no es valido", errors.get("email"));
        assertEquals("El nombre es obligatorio", errors.get("name"));

    }

    private void dummyMethod(String value) {
    }

    @Test
    void shouldHandleIllegalArgumentException() {

        IllegalArgumentException exception = new IllegalArgumentException("La reserva ya tiene un pago asociado");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.argumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("La reserva ya tiene un pago asociado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());

    }

    @Test
    void shouldHandleGenericException() {

        Exception exception = new Exception("Error de base de datos");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.exception(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Error interno del servidor", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());

    }
}
