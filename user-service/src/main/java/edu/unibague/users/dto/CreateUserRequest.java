package edu.unibague.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite maximo 80 caracteres")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 80, message = "El apellido admite maximo 80 caracteres")
        String lastName,

        @Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "El telefono debe tener entre 7 y 20 digitos")
        String phone) {
}
