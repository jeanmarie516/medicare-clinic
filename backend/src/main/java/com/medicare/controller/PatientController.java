package com.medicare.controller;

import com.medicare.dto.PatientDto;
import com.medicare.entity.User;
import com.medicare.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patients", description = "Gestion des patients")
public class PatientController {


    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Liste des patients", description = "Liste paginée des patients avec recherche")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Page<PatientDto>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(patientService.getAllPatients(page, size, search));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des patients", description = "Recherche rapide de patients pour autocomplete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<PatientDto>> searchPatients(@RequestParam String q) {
        return ResponseEntity.ok(patientService.searchPatients(q));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un patient", description = "Fiche complète d'un patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un patient", description = "Ajouter un nouveau patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<PatientDto> createPatient(
            @Valid @RequestBody PatientDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(patientService.createPatient(dto, user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un patient", description = "Mettre à jour les informations d'un patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<PatientDto> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(patientService.updatePatient(id, dto, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient", description = "Supprimer un patient")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        patientService.deletePatient(id, user);
        return ResponseEntity.noContent().build();
    }
}
