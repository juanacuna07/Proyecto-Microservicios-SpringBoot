package edu.unibague.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unibague.auth.dto.AuthResponse;
import edu.unibague.auth.dto.LoginRequest;
import edu.unibague.auth.dto.RegisterRequest;
import edu.unibague.auth.dto.RegisterResponse;
import edu.unibague.auth.exception.ConflictException;
import edu.unibague.auth.model.Credential;
import edu.unibague.auth.model.Role;
import edu.unibague.auth.repository.CredentialRepository;

@Service
public class AuthService {

    private final CredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(CredentialRepository repository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Registra un usuario nuevo con rol USER. */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();

        if (repository.existsByUsername(username)) {
            throw new ConflictException("El username ya esta registrado");
        }
        if (repository.existsByEmail(email)) {
            throw new ConflictException("El email ya esta registrado");
        }

        Credential saved = repository.save(new Credential(username, email,
                passwordEncoder.encode(request.password()), Role.USER));
        return new RegisterResponse(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole().name());
    }

    /** Valida usuario y contrasena (BCrypt) y devuelve un JWT. */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        Credential credential = repository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));
        return jwtService.issueToken(credential);
    }
}
