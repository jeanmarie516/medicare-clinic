package com.medicare.controller;

import com.medicare.dto.AuditLogDto;
import com.medicare.dto.AuthResponse;
import com.medicare.dto.RegisterRequest;
import com.medicare.entity.Role;
import com.medicare.entity.User;
import com.medicare.repository.UserRepository;
import com.medicare.service.AdminService;
import com.medicare.service.AuditService;
import com.medicare.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administration", description = "Gestion des utilisateurs et audit (Admin uniquement)")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private final UserRepository userRepository;
    private final AuthService authService;
    private final AuditService auditService;
    private final AdminService adminService;

    public AdminController(UserRepository userRepository, AuthService authService, AuditService auditService, AdminService adminService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.auditService = auditService;
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "Liste des utilisateurs", description = "Liste de tous les utilisateurs")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = users.stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "nom", u.getNom(),
                        "prenom", u.getPrenom(),
                        "email", u.getEmail(),
                        "role", u.getRole().name(),
                        "actif", u.getActif(),
                        "telephone", u.getTelephone() != null ? u.getTelephone() : "",
                        "photoUrl", u.getPhotoUrl() != null ? u.getPhotoUrl() : "",
                        "createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : ""
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/users")
    @Operation(summary = "Créer un utilisateur", description = "Créer un compte médecin ou secrétaire")
    public ResponseEntity<AuthResponse> createUser(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/users/{id}/toggle-actif")
    @Operation(summary = "Activer/Désactiver", description = "Activer ou désactiver un compte utilisateur")
    public ResponseEntity<Map<String, Object>> toggleActif(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setActif(!user.getActif());
        userRepository.save(user);

        auditService.log(user.getActif() ? "ACTIVATION" : "DESACTIVATION",
                "UTILISATEUR", "Compte " + user.getEmail() + " " +
                        (user.getActif() ? "activé" : "désactivé"), admin);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "actif", user.getActif(),
                "message", "Compte " + (user.getActif() ? "activé" : "désactivé") + " avec succès"
        ));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprimer un médecin ou secrétaire inactif")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin) {
        adminService.deleteUser(id, admin);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Journal d'audit", description = "Liste paginée des actions journalisées")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(auditService.getAuditLogs(PageRequest.of(page, size)));
    }
}
