package com.medicare.controller;

import com.medicare.dto.DashboardStats;
import com.medicare.entity.Medecin;
import com.medicare.entity.User;
import com.medicare.repository.MedecinRepository;
import com.medicare.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Statistiques et indicateurs")
public class DashboardController {

    private final DashboardService dashboardService;
    private final MedecinRepository medecinRepository;

    public DashboardController(DashboardService dashboardService, MedecinRepository medecinRepository) {
        this.dashboardService = dashboardService;
        this.medecinRepository = medecinRepository;
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques admin", description = "Statistiques globales pour le tableau de bord admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats> getAdminStats() {
        return ResponseEntity.ok(dashboardService.getAdminStats());
    }

    @GetMapping("/medecin-stats")
    @Operation(summary = "Statistiques médecin", description = "Statistiques pour le tableau de bord du médecin connecté")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<DashboardStats> getMedecinStats(@AuthenticationPrincipal User user) {
        Medecin medecin = medecinRepository.findAll().stream()
                .filter(m -> m.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Profil médecin non trouvé"));
        return ResponseEntity.ok(dashboardService.getMedecinStats(medecin));
    }
}
