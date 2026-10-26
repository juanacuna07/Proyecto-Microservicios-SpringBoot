package edu.unibague.auth.service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;

import edu.unibague.auth.dto.AuthResponse;
import edu.unibague.auth.model.Credential;

/**
 * Emite JWT firmados con RS256 (llave privada RSA).
 * Claims: sub = id del usuario, username, email, roles, iss, iat, exp.
 */
@Service
public class JwtService {

    public static final String KEY_ID = "taller-02-rsa";

    private final JwtEncoder encoder;
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(RSAPublicKey publicKey,
                      RSAPrivateKey privateKey,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(KEY_ID).build();
        this.encoder = new NimbusJwtEncoder(new ImmutableJWKSet<SecurityContext>(new JWKSet(rsaKey)));
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    public AuthResponse issueToken(Credential credential) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(credential.getId().toString())
                .claim("username", credential.getUsername())
                .claim("email", credential.getEmail())
                .claim("roles", List.of(credential.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).keyId(KEY_ID).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AuthResponse("Bearer", token, expiresAt, credential.getId(),
                credential.getUsername(), credential.getRole().name());
    }
}
