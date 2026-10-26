package edu.unibague.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.unibague.auth.model.Credential;
import edu.unibague.auth.model.Role;
import edu.unibague.auth.repository.CredentialRepository;

/**
 * Crea los usuarios de prueba del taller si no existen:
 *   admin / Admin123!     (ADMIN)
 *   student / Student123! (USER)
 * Solo para ambiente local. Desactivar con DEMO_DATA_ENABLED=false.
 */
@Configuration
public class DemoDataConfig {

    private static final Logger log = LoggerFactory.getLogger(DemoDataConfig.class);

    @Bean
    ApplicationRunner demoUsers(CredentialRepository repository, PasswordEncoder encoder,
                                @Value("${app.demo-data.enabled:true}") boolean enabled) {
        return args -> {
            if (!enabled) {
                return;
            }
            create(repository, encoder, "admin", "admin@taller.local", "Admin123!", Role.ADMIN);
            create(repository, encoder, "student", "student@taller.local", "Student123!", Role.USER);
        };
    }

    private void create(CredentialRepository repository, PasswordEncoder encoder,
                        String username, String email, String password, Role role) {
        if (!repository.existsByUsername(username)) {
            repository.save(new Credential(username, email, encoder.encode(password), role));
            log.info("Usuario demo creado: {} ({})", username, role);
        }
    }
}
