package com.medicare.controller;

import com.medicare.dto.PrescriptionDto;
import com.medicare.entity.Prescription;
import com.medicare.entity.User;
import com.medicare.service.PrescriptionService;
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

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/prescriptions")
@Tag(name = "Prescriptions", description = "Gestion des prescriptions médicales")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    @Operation(summary = "Liste des prescriptions", description = "Liste paginée des prescriptions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<Page<PrescriptionDto>> getAllPrescriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'une prescription", description = "Informations complètes sur une prescription")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<PrescriptionDto> getPrescription(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Prescriptions d'un patient", description = "Liste des prescriptions d'un patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<java.util.List<PrescriptionDto>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatient(patientId));
    }

    @PostMapping
    @Operation(summary = "Créer une prescription", description = "Créer une nouvelle prescription (médecin uniquement)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<PrescriptionDto> createPrescription(
            @Valid @RequestBody PrescriptionDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(prescriptionService.createPrescription(dto, user));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Télécharger PDF", description = "Télécharger la prescription au format PDF")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionEntity(id);
            String pdfUrl = prescription.getPdfUrl();

            if (pdfUrl == null || pdfUrl.isBlank()) {
                prescription = prescriptionService.generatePdfForPrescription(id);
                pdfUrl = prescription.getPdfUrl();
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
                prescription = prescriptionService.generatePdfForPrescription(id);
                pdfUrl = prescription.getPdfUrl();
                filePath = Paths.get(pdfStoragePath).resolve(pdfUrl).normalize();
                resource = new UrlResource(filePath.toUri());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"prescription_" + id + ".pdf\"")
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du téléchargement du PDF", e);
        }
    }
}
