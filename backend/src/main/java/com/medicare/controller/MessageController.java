package com.medicare.controller;

import com.medicare.dto.MessageDto;
import com.medicare.entity.User;
import com.medicare.repository.UserRepository;
import com.medicare.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
@Tag(name = "Messages", description = "Messagerie interne")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @GetMapping("/conversation/{userId}")
    @Operation(summary = "Conversation", description = "Récupérer la conversation entre deux utilisateurs")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<MessageDto>> getConversation(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getConversation(user.getId(), userId));
    }

    @PostMapping
    @Operation(summary = "Envoyer un message", description = "Envoyer un message à un autre utilisateur")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<MessageDto> sendMessage(
            @Valid @RequestBody MessageDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.sendMessage(dto, user));
    }

    @PostMapping("/{messageId}/lu")
    @Operation(summary = "Marquer comme lu", description = "Marquer un message comme lu")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Void> marquerCommeLu(
            @PathVariable Long messageId,
            @AuthenticationPrincipal User user) {
        messageService.marquerCommeLu(messageId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/conversation/{userId}/lu")
    @Operation(summary = "Marquer conversation comme lue", description = "Marquer tous les messages d'une conversation comme lus")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Void> marquerConversationCommeLu(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        messageService.marquerConversationCommeLu(userId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/non-lus")
    @Operation(summary = "Messages non lus", description = "Nombre de messages non lus")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<Map<String, Long>> getMessagesNonLus(@AuthenticationPrincipal User user) {
        long count = messageService.getNombreMessagesNonLus(user);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/contacts")
    @Operation(summary = "Contacts", description = "Liste des utilisateurs avec qui on a conversé")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<Map<String, Object>>> getContacts(@AuthenticationPrincipal User user) {
        List<User> contacts = messageService.getConversationPartners(user);
        List<Map<String, Object>> result = contacts.stream()
                .map(c -> Map.<String, Object>of(
                        "id", c.getId(),
                        "nom", c.getNom(),
                        "prenom", c.getPrenom(),
                        "email", c.getEmail(),
                        "role", c.getRole().name(),
                        "photoUrl", c.getPhotoUrl() != null ? c.getPhotoUrl() : ""
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search-users")
    @Operation(summary = "Rechercher utilisateurs", description = "Rechercher des utilisateurs pour démarrer une nouvelle conversation")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(
            @RequestParam(defaultValue = "") String q,
            @AuthenticationPrincipal User currentUser) {
        List<User> users;
        if (q == null || q.isBlank()) {
            users = userRepository.findAll().stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()) && u.getActif())
                    .collect(Collectors.toList());
        } else {
            users = userRepository.search(q).stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()) && u.getActif())
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> result = users.stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "nom", u.getNom(),
                        "prenom", u.getPrenom(),
                        "email", u.getEmail(),
                        "role", u.getRole().name(),
                        "photoUrl", u.getPhotoUrl() != null ? u.getPhotoUrl() : ""
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
