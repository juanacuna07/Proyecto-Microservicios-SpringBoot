package edu.unibague.users.dto;

import java.time.Instant;
import java.util.UUID;

import edu.unibague.users.model.UserProfile;

/** DTO de salida: la entidad JPA nunca se expone directamente. */
public record UserResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Instant createdAt,
        Instant updatedAt) {

    public static UserResponse from(UserProfile p) {
        return new UserResponse(p.getId(), p.getUsername(), p.getEmail(), p.getFirstName(),
                p.getLastName(), p.getPhone(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
