package com.medicare.controller;

import com.medicare.dto.MedecinDto;
import com.medicare.entity.User;
import com.medicare.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medecins")
@Tag(name = "Médecins", description = "Gestion des médecins")
public class MedecinController {


    private final MedecinService medecinService;

    public MedecinController(MedecinService medecinService) {
        this.medecinService = medecinService;
    }

    @GetMapping
    @Operation(summary = "Liste des médecins", description = "Liste de tous les médecins")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<MedecinDto>> getAllMedecins() {
        return ResponseEntity.ok(medecinService.getAllMedecins());
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Médecins disponibles", description = "Liste des médecins disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<MedecinDto>> getMedecinsDisponibles() {
        return ResponseEntity.ok(medecinService.getMedecinsDisponibles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un médecin", description = "Fiche complète d'un médecin")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<MedecinDto> getMedecin(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.getMedecinById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Médecin par utilisateur", description = "Récupérer le profil médecin par ID utilisateur")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<MedecinDto> getMedecinByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(medecinService.getMedecinByUserId(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un médecin", description = "Mettre à jour les informations d'un médecin")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<MedecinDto> updateMedecin(
            @PathVariable Long id,
            @RequestBody MedecinDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(medecinService.updateMedecin(id, dto, user));
    }
}
