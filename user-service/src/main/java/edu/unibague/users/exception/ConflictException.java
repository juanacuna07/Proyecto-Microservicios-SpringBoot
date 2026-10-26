package edu.unibague.users.exception;

/** El perfil del usuario ya existe (HTTP 409). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
