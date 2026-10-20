package com.reservas.apidereservas.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.reservas.apidereservas.dto.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> exceptionHandler(ResourceNotFoundException resourceNotFoundException) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponseDTO.setMessage(resourceNotFoundException.getMessage());
        errorResponseDTO.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponseDTO);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> argumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
       
         ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
         
         List<FieldError> fieldErrors = methodArgumentNotValidException
                .getBindingResult()
                .getFieldErrors();

        Map<String, String> errors = fieldErrors.stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (firstMessage, secondMessage) -> firstMessage));

        errorResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDTO.setMessage("Error de validacion en los datos enviados");
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setErrors(errors);

        return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseDTO);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> argumentException(IllegalArgumentException illegalArgumentException) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDTO.setMessage(illegalArgumentException.getMessage());
        errorResponseDTO.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDTO);

    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> authorizationDeniedException(
        AuthorizationDeniedException exception) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

            errorResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
            errorResponseDTO.setMessage("No tienes permisos para realizar esta operacion");
            errorResponseDTO.setTimestamp(LocalDateTime.now());

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(errorResponseDTO);
        }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> exception(Exception exception) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

        errorResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponseDTO.setMessage("Error interno del servidor");
        errorResponseDTO.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseDTO);

    }

}
