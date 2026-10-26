package edu.unibague.auth.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Taller 02 v2.0 - Auth Service",
        version = "2.0.0",
        description = "Registro, autenticacion y emision de JWT con RSA."))
public class OpenApiConfig {
}
