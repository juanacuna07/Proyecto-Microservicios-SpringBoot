package edu.unibague.auth.dto;

import java.util.UUID;

/** Respuesta del registro. Nunca incluye el passwordHash. */
public record RegisterResponse(UUID userId, String username, String email, String role) {
}
