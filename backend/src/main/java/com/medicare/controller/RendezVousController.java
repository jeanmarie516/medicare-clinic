package com.medicare.controller;

import com.medicare.dto.RendezVousDto;
import com.medicare.entity.StatutRendezVous;
import com.medicare.entity.User;
import com.medicare.service.RendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rendez-vous")
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous médicaux")
public class RendezVousController {


    private final RendezVousService rendezVousService;

    public RendezVousController(RendezVousService rendezVousService) {
        this.rendezVousService = rendezVousService;
    }

    @GetMapping
    @Operation(summary = "Liste des rendez-vous", description = "Liste paginée des rendez-vous avec filtres")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Page<RendezVousDto>> getAllRendezVous(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long medecinId) {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous(page, size, search, medecinId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un rendez-vous", description = "Informations complètes sur un rendez-vous")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<RendezVousDto> getRendezVous(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousById(id));
    }

    @GetMapping("/medecin/{medecinId}/date/{date}")
    @Operation(summary = "RDV par médecin et date", description = "Liste des rendez-vous d'un médecin pour une date donnée")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<RendezVousDto>> getRendezVousByMedecinAndDate(
            @PathVariable Long medecinId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByMedecinAndDate(medecinId, date));
    }

    @GetMapping("/calendrier")
    @Operation(summary = "RDV pour le calendrier", description = "Liste des rendez-vous entre deux dates pour l'affichage calendrier")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<RendezVousDto>> getRendezVousForCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(required = false) Long medecinId,
            @RequestParam(required = false) StatutRendezVous statut) {
        return ResponseEntity.ok(rendezVousService.getRendezVousWithFilters(debut, fin, medecinId, statut));
    }

    @PostMapping
    @Operation(summary = "Créer un rendez-vous", description = "Créer un nouveau rendez-vous avec vérification des conflits")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<RendezVousDto> createRendezVous(
            @Valid @RequestBody RendezVousDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rendezVousService.createRendezVous(dto, user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un rendez-vous", description = "Mettre à jour un rendez-vous existant")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<RendezVousDto> updateRendezVous(
            @PathVariable Long id,
            @RequestBody RendezVousDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, dto, user));
    }

    @PostMapping("/{id}/annuler")
    @Operation(summary = "Annuler un rendez-vous", description = "Annulation avec règle des 24h (sauf admin)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<RendezVousDto> annulerRendezVous(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rendezVousService.annulerRendezVous(id, user));
    }

    @PostMapping("/{id}/statut")
    @Operation(summary = "Changer statut RDV", description = "Mettre à jour le statut d'un rendez-vous")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<RendezVousDto> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutRendezVous statut,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(rendezVousService.changerStatut(id, statut, user));
    }
}
