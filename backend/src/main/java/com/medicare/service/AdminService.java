package com.medicare.service;

import com.medicare.entity.Medecin;
import com.medicare.entity.Role;
import com.medicare.entity.User;
import com.medicare.exception.BadRequestException;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditService auditService;

    public AdminService(UserRepository userRepository, MedecinRepository medecinRepository,
                        NotificationRepository notificationRepository,
                        AuditLogRepository auditLogRepository,
                        AuditService auditService) {
        this.userRepository = userRepository;
        this.medecinRepository = medecinRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
        this.auditService = auditService;
    }

    @Transactional
    public void deleteUser(Long userId, User admin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Impossible de supprimer un administrateur");
        }

        // Supprimer le profil médecin si existe
        medecinRepository.findByUserId(userId).ifPresent(medecinRepository::delete);

        // Supprimer les notifications associées
        notificationRepository.deleteByUser(user);

        // Supprimer les logs d'audit associés (sauf celui qu'on va créer)
        auditLogRepository.deleteByUser(user);

        try {
            // Supprimer l'utilisateur (flush forcé pour capturer les erreurs FK)
            userRepository.delete(user);
            userRepository.flush();
        } catch (Exception e) {
            throw new BadRequestException(
                    "Impossible de supprimer cet utilisateur : il a des rendez-vous ou factures associés. " +
                    "Désactivez le compte à la place.");
        }

        // Journaliser après suppression (pour que le log ne soit pas supprimé)
        auditService.log("SUPPRESSION", "UTILISATEUR",
                "Suppression du compte " + user.getEmail() + " (" + user.getRole() + ")", admin);
    }
}
