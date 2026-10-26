package edu.unibague.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.unibague.users.dto.CreateUserRequest;
import edu.unibague.users.dto.UpdateUserRequest;
import edu.unibague.users.dto.UserResponse;
import edu.unibague.users.exception.ConflictException;
import edu.unibague.users.exception.ResourceNotFoundException;
import edu.unibague.users.model.UserProfile;
import edu.unibague.users.repository.UserProfileRepository;

@Service
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    /** Crea el perfil del usuario autenticado. Identidad tomada del JWT (sub, username, email). */
    @Transactional
    public UserResponse create(Jwt jwt, CreateUserRequest request) {
        UUID id = subjectOf(jwt);
        if (repository.existsById(id)) {
            throw new ConflictException("El perfil del usuario ya existe");
        }
        UserProfile profile = new UserProfile(id,
                jwt.getClaimAsString("username"),
                jwt.getClaimAsString("email"),
                request.firstName().trim(),
                request.lastName().trim(),
                request.phone());
        return UserResponse.from(repository.save(profile));
    }

    @Transactional(readOnly = true)
    public UserResponse findMine(Jwt jwt) {
        return UserResponse.from(get(subjectOf(jwt)));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return repository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id, Jwt jwt, Authentication authentication) {
        checkOwnerOrAdmin(id, jwt, authentication);
        return UserResponse.from(get(id));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request, Jwt jwt, Authentication authentication) {
        checkOwnerOrAdmin(id, jwt, authentication);
        UserProfile profile = get(id);
        profile.update(request.firstName().trim(), request.lastName().trim(), request.phone());
        return UserResponse.from(repository.saveAndFlush(profile));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(get(id));
    }

    private UserProfile get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado: " + id));
    }

    /** Autorizacion por identidad/rol: el propietario o un ADMIN. */
    static void checkOwnerOrAdmin(UUID id, Jwt jwt, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin && !id.equals(subjectOf(jwt))) {
            throw new AccessDeniedException("No tiene permiso para acceder a este perfil");
        }
    }

    private static UUID subjectOf(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
