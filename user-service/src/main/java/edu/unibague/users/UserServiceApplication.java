package edu.unibague.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de perfiles (puerto 8082).
 * Actua como OAuth2 Resource Server: valida el JWT solo con la llave PUBLICA RSA.
 * Persistencia propia: base de datos users_db (patron Database per Service).
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
