package edu.unibague.users.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

/** Formato uniforme de error para toda la API. */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors) {

    public static ApiError of(HttpStatus status, String message, String path, Map<String, String> validationErrors) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path, validationErrors);
    }
}
