package com.medicare.service;

import com.medicare.entity.RendezVous;
import com.medicare.entity.StatutRendezVous;
import com.medicare.repository.NotificationRepository;
import com.medicare.repository.RendezVousRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RendezVousReminderService {

    private static final Logger log = LoggerFactory.getLogger(RendezVousReminderService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    private final RendezVousRepository rendezVousRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public RendezVousReminderService(RendezVousRepository rendezVousRepository,
                                     NotificationRepository notificationRepository,
                                     NotificationService notificationService) {
        this.rendezVousRepository = rendezVousRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Envoie automatiquement un rappel aux médecins pour les rendez-vous
     * confirmés ou en attente qui ont lieu dans les 2 prochaines heures.
     * Exécution toutes les 15 minutes. Un seul rappel par RDV (anti-doublon).
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void envoyerRappelsProches() {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime debut = maintenant.plusMinutes(30);
        LocalDateTime fin = maintenant.plusHours(2);

        List<RendezVous> rdvs = rendezVousRepository.findRendezVousBetween(debut, fin);
        for (RendezVous rdv : rdvs) {
            StatutRendezVous statut = rdv.getStatut();
            if (statut != StatutRendezVous.CONFIRME && statut != StatutRendezVous.EN_ATTENTE) {
                continue;
            }
            if (rdv.getMedecin().getUser() == null) {
                continue;
            }
            String lien = "/rendez-vous/" + rdv.getId();
            // Anti-doublon : ne pas renvoyer le rappel si un RAPPEL_RDV existe déjà pour ce RDV
            if (notificationRepository.existsByTypeAndLien("RAPPEL_RDV", lien)) {
                continue;
            }
            String patient = rdv.getPatient().getPrenom() + " " + rdv.getPatient().getNom();
            String contenu = "Rendez-vous avec " + patient + " le " +
                    rdv.getDateHeure().format(FORMATTER) +
                    (rdv.getMotif() != null && !rdv.getMotif().isBlank() ? " — " + rdv.getMotif() : "");
            notificationService.createNotification(
                    rdv.getMedecin().getUser(),
                    "RAPPEL_RDV",
                    "Rappel de rendez-vous",
                    contenu,
                    lien
            );
            log.info("Rappel RDV #{} envoyé à Dr {} {} ({} à {})",
                    rdv.getId(), rdv.getMedecin().getUser().getPrenom(),
                    rdv.getMedecin().getUser().getNom(), patient, rdv.getDateHeure());
        }
    }
}
