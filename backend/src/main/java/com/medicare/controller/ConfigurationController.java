package com.medicare.controller;

import com.medicare.dto.ConfigurationDto;
import com.medicare.entity.User;
import com.medicare.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/configurations")
@Tag(name = "Configurations", description = "Paramètres de configuration de la clinique")
@PreAuthorize("hasRole('ADMIN')")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les configurations")
    public ResponseEntity<Map<String, String>> getAll() {
        return ResponseEntity.ok(configurationService.getAll());
    }

    @GetMapping("/prefix/{prefix}")
    @Operation(summary = "Récupérer les configurations par préfixe")
    public ResponseEntity<Map<String, String>> getByPrefix(@PathVariable String prefix) {
        return ResponseEntity.ok(configurationService.getByPrefix(prefix + "."));
    }

    @GetMapping("/{cle}")
    @Operation(summary = "Récupérer une configuration par clé")
    public ResponseEntity<Map<String, String>> getValue(@PathVariable String cle) {
        return ResponseEntity.ok(Map.of("cle", cle, "valeur", configurationService.getValue(cle)));
    }

    @PostMapping
    @Operation(summary = "Sauvegarder les configurations")
    public ResponseEntity<ConfigurationDto> saveAll(
            @RequestBody Map<String, String> configs,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(configurationService.saveAll(configs, admin));
    }

    @PostMapping("/reset")
    @Operation(summary = "Réinitialiser les configurations par défaut")
    public ResponseEntity<ConfigurationDto> resetDefaults(@AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(configurationService.resetDefaults(admin));
    }
}
