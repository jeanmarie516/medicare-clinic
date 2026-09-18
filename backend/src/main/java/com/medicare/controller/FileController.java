package com.medicare.controller;

import com.medicare.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
@Tag(name = "Fichiers", description = "Téléchargement de fichiers (PDF, etc.)")
public class FileController {

    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    @GetMapping("/{type}/{filename:.+}")
    @Operation(summary = "Télécharger un fichier", description = "Télécharger un fichier PDF (prescription, facture)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String type,
            @PathVariable String filename) {

        try {
            Path storagePath = Paths.get(pdfStoragePath).toAbsolutePath().normalize();
            Path typeDir = storagePath.resolve(type).normalize();
            Path filePath = typeDir.resolve(filename).normalize();

            // Sécurité : empêcher les attaques de type path traversal
            if (!filePath.startsWith(typeDir)) {
                throw new AccessDeniedException("Accès au fichier refusé");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Fichier non trouvé: " + filename);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Fichier non trouvé: " + filename);
        }
    }
}
