package com.medicare.controller;

import com.medicare.entity.User;
import com.medicare.service.UserPresenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Tag(name = "Présence", description = "Statut de présence en ligne des utilisateurs")
public class UserPresenceController {

    private final UserPresenceService presenceService;

    public UserPresenceController(UserPresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @GetMapping("/online")
    @Operation(summary = "Utilisateurs en ligne", description = "Retourne les IDs des utilisateurs connectés au WebSocket")
    public ResponseEntity<Map<String, Set<Long>>> getOnlineUsers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("onlineUserIds", presenceService.getOnlineUserIds()));
    }

    @GetMapping("/me/online")
    @Operation(summary = "Mon statut", description = "Vérifie si l'utilisateur courant est considéré comme en ligne")
    public ResponseEntity<Map<String, Boolean>> checkMyOnlineStatus(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("online", presenceService.isOnline(user.getId())));
    }
}
