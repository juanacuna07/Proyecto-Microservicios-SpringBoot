package edu.unibague.auth.config;

import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.unibague.auth.security.PemKeyLoader;

/**
 * Carga el par de llaves RSA. En Docker los archivos llegan por volumenes
 * (jwt-private y jwt-public) creados por el servicio key-generator.
 * La llave privada solo existe en auth-service.
 */
@Configuration
public class RsaKeyConfig {

    @Bean
    RSAPrivateKey rsaPrivateKey(@Value("${app.jwt.private-key-path}") String path) {
        return PemKeyLoader.loadPrivateKey(Path.of(path));
    }

    @Bean
    RSAPublicKey rsaPublicKey(@Value("${app.jwt.public-key-path}") String path) {
        return PemKeyLoader.loadPublicKey(Path.of(path));
    }
}
