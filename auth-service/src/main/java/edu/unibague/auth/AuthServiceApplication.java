package edu.unibague.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de autenticacion (puerto 8081).
 * Responsabilidades: registro, login con BCrypt y emision de JWT firmado con llave privada RSA.
 * Persistencia propia: base de datos auth_db (patron Database per Service).
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
