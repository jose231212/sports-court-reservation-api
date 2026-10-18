package com.reservas.apidereservas.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class UserRequestDTO {

    @Schema(
        description = "User's full name",
        example = "Pedro Fernandez",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 20
    )
    @NotBlank
    @Size(max = 20)
    private String name;

    @Schema(
        description = "User's email address",
        example = "pedro@gmail.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    @Email 
    private String email;

    @Schema(
        description = "User's password",
        example = "password",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 8,
        maxLength = 100
    )
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

}
