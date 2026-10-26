package edu.unibague.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway: punto de entrada unico de la solucion (puerto 8080).
 * Enruta /api/auth/** hacia auth-service y /api/users/** hacia user-service.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
