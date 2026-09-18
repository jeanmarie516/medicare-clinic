package com.medicare.controller;

import com.medicare.dto.NotificationDto;
import com.medicare.entity.User;
import com.medicare.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications")
public class NotificationController {


    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/non-lues")
    @Operation(summary = "Notifications non lues", description = "Liste des notifications non lues")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<NotificationDto>> getNotificationsNonLues(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(user));
    }

    @GetMapping
    @Operation(summary = "Toutes les notifications", description = "Liste paginée de toutes les notifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Page<NotificationDto>> getAllNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.getAllNotifications(user, page, size));
    }

    @GetMapping("/count")
    @Operation(summary = "Nombre de notifications", description = "Nombre de notifications non lues")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Map<String, Long>> getNotificationCount(@AuthenticationPrincipal User user) {
        long count = notificationService.getNombreNotificationsNonLues(user);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/{id}/lu")
    @Operation(summary = "Marquer comme lue", description = "Marquer une notification comme lue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Void> marquerCommeLu(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        notificationService.marquerCommeLu(id, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tout-lu")
    @Operation(summary = "Tout marquer comme lu", description = "Marquer toutes les notifications comme lues")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Void> marquerToutCommeLu(@AuthenticationPrincipal User user) {
        notificationService.marquerToutCommeLu(user);
        return ResponseEntity.ok().build();
    }
}
