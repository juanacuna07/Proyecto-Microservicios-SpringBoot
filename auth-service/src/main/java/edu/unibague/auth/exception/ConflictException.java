package edu.unibague.auth.exception;

/** Se lanza cuando el username o el email ya estan registrados (HTTP 409). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
