package edu.unibague.users.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.unibague.users.dto.CreateUserRequest;
import edu.unibague.users.dto.UpdateUserRequest;
import edu.unibague.users.dto.UserResponse;
import edu.unibague.users.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "user-controller")
public class UserController {

    private final UserProfileService service;

    public UserController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crea el perfil del usuario autenticado")
    public ResponseEntity<UserResponse> create(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(jwt, request));
    }

    @GetMapping("/me")
    @Operation(summary = "Obtiene el perfil del usuario autenticado")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return service.findMine(jwt);
    }

    @GetMapping
    @Operation(summary = "Lista todos los perfiles (solo ADMIN)")
    public List<UserResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un perfil (propio o ADMIN cualquier perfil)")
    public UserResponse get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return service.findById(id, jwt, authentication);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un perfil (propio o ADMIN cualquier perfil)")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request,
                               @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return service.update(id, request, jwt, authentication);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un perfil (solo ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
