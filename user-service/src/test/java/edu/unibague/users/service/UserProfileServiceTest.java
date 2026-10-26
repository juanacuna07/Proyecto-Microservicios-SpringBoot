package edu.unibague.users.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class UserProfileServiceTest {

    private Jwt jwtFor(UUID subject) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(subject.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
    }

    @Test
    void elPropietarioPuedeAccederASuPerfil() {
        UUID id = UUID.randomUUID();
        Jwt jwt = jwtFor(id);
        var auth = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        assertDoesNotThrow(() -> UserProfileService.checkOwnerOrAdmin(id, jwt, auth));
    }

    @Test
    void unUsuarioNoPuedeAccederAUnPerfilAjeno() {
        Jwt jwt = jwtFor(UUID.randomUUID());
        var auth = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        assertThrows(AccessDeniedException.class,
                () -> UserProfileService.checkOwnerOrAdmin(UUID.randomUUID(), jwt, auth));
    }

    @Test
    void unAdminPuedeAccederACualquierPerfil() {
        Jwt jwt = jwtFor(UUID.randomUUID());
        var auth = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertDoesNotThrow(() -> UserProfileService.checkOwnerOrAdmin(UUID.randomUUID(), jwt, auth));
    }
}
