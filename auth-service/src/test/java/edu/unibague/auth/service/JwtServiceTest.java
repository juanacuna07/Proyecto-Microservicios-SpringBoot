package edu.unibague.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import edu.unibague.auth.dto.AuthResponse;
import edu.unibague.auth.model.Credential;
import edu.unibague.auth.model.Role;

class JwtServiceTest {

    @Test
    void elTokenFirmadoSeValidaConLaLlavePublica() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();

        JwtService service = new JwtService(publicKey, (RSAPrivateKey) pair.getPrivate(), "test-issuer", 60);
        Credential credential = new Credential("student", "student@taller.local", "hash", Role.USER);

        AuthResponse response = service.issueToken(credential);
        Jwt jwt = NimbusJwtDecoder.withPublicKey(publicKey).build().decode(response.accessToken());

        assertEquals("Bearer", response.tokenType());
        assertEquals(credential.getId().toString(), jwt.getSubject());
        assertEquals("student", jwt.getClaimAsString("username"));
        assertEquals(List.of("USER"), jwt.getClaimAsStringList("roles"));
        assertTrue(jwt.getExpiresAt().isAfter(jwt.getIssuedAt()));
    }
}
