package com.medicare.controller;

import com.medicare.dto.FactureDto;
import com.medicare.entity.Facture;
import com.medicare.entity.User;
import com.medicare.service.FactureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/factures")
@Tag(name = "Factures", description = "Gestion des factures")
public class FactureController {

    private final FactureService factureService;

    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @GetMapping
    @Operation(summary = "Liste des factures", description = "Liste paginée des factures")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Page<FactureDto>> getAllFactures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(factureService.getAllFactures(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'une facture", description = "Informations complètes sur une facture")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<FactureDto> getFacture(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Factures d'un patient", description = "Liste des factures d'un patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<FactureDto>> getFacturesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(factureService.getFacturesByPatient(patientId));
    }

    @GetMapping("/impayees")
    @Operation(summary = "Factures impayées", description = "Liste des factures en attente de paiement")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<List<FactureDto>> getFacturesImpayees() {
        return ResponseEntity.ok(factureService.getFacturesImpayees());
    }

    @PostMapping
    @Operation(summary = "Créer une facture", description = "Créer une nouvelle facture")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<FactureDto> createFacture(
            @Valid @RequestBody FactureDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(factureService.createFacture(dto, user));
    }

    @PostMapping("/{id}/payer")
    @Operation(summary = "Marquer comme payée", description = "Enregistrer le paiement complet d'une facture")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<FactureDto> marquerPayee(
            @PathVariable Long id,
            @RequestParam(required = false) String modePaiement,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(factureService.marquerPayee(id, modePaiement, user));
    }

    @PostMapping("/{id}/paiement-partiel")
    @Operation(summary = "Paiement partiel", description = "Enregistrer un paiement partiel sur une facture")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<FactureDto> paiementPartiel(
            @PathVariable Long id,
            @RequestParam BigDecimal montant,
            @RequestParam(required = false) String modePaiement,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(factureService.enregistrerPaiementPartiel(id, montant, modePaiement, user));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Télécharger PDF", description = "Télécharger la facture au format PDF")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        try {
            Facture facture = factureService.getFactureEntity(id);
            String pdfUrl = facture.getPdfUrl();

            if (pdfUrl == null || pdfUrl.isBlank()) {
                facture = factureService.generatePdfForFacture(id);
                pdfUrl = facture.getPdfUrl();
            }

            Path filePath;
            if (pdfUrl.startsWith("./") || pdfUrl.startsWith("/")) {
                String relativePart = pdfUrl.replace("./pdf-storage/", "").replace(pdfStoragePath + "/", "");
                filePath = Paths.get(pdfStoragePath).resolve(relativePart).normalize();
            } else {
                filePath = Paths.get(pdfStoragePath).resolve(pdfUrl).normalize();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                facture = factureService.generatePdfForFacture(id);
                pdfUrl = facture.getPdfUrl();
                filePath = Paths.get(pdfStoragePath).resolve(pdfUrl).normalize();
                resource = new UrlResource(filePath.toUri());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"facture_" + id + ".pdf\"")
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du téléchargement du PDF", e);
        }
    }
}
