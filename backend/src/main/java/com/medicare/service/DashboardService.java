package com.medicare.service;

import com.medicare.dto.DashboardStats;
import com.medicare.entity.*;
import com.medicare.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {


    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezVousRepository;
    private final FactureRepository factureRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardService(UserRepository userRepository, PatientRepository patientRepository, MedecinRepository medecinRepository, RendezVousRepository rendezVousRepository, FactureRepository factureRepository, PrescriptionRepository prescriptionRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.factureRepository = factureRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public DashboardStats getAdminStats() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDateTime debutJour = aujourdhui.atStartOfDay();
        LocalDateTime finJour = aujourdhui.atTime(LocalTime.MAX);
        LocalDate debutMois = aujourdhui.withDayOfMonth(1);
        LocalDate finMois = aujourdhui.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime debutMoisDateTime = debutMois.atStartOfDay();
        LocalDateTime finMoisDateTime = finMois.atTime(LocalTime.MAX);

        long totalPatients = patientRepository.count();
        long totalMedecins = userRepository.countByRole(Role.MEDECIN);
        long totalSecretaires = userRepository.countByRole(Role.SECRETAIRE);
        long rdvAujourdhui = rendezVousRepository.countRendezVousBetween(debutJour, finJour);
        long rdvEnAttente = rendezVousRepository.findRendezVousBetween(debutJour, finJour).stream()
                .filter(r -> r.getStatut() == StatutRendezVous.EN_ATTENTE).count();
        long rdvConfirmes = rendezVousRepository.findRendezVousBetween(debutJour, finJour).stream()
                .filter(r -> r.getStatut() == StatutRendezVous.CONFIRME).count();
        long rdvTermines = rendezVousRepository.findRendezVousBetween(debutJour, finJour).stream()
                .filter(r -> r.getStatut() == StatutRendezVous.TERMINE).count();
        long rdvAnnules = rendezVousRepository.findRendezVousBetween(debutJour, finJour).stream()
                .filter(r -> r.getStatut() == StatutRendezVous.ANNULE).count();

        Double revenusMois = factureRepository.sumMontantPayeBetween(debutMois, finMois);
        Double revenusJour = factureRepository.sumMontantPayeBetween(aujourdhui, aujourdhui);

        long facturesImpayees = factureRepository.countByStatutPaiement(StatutPaiement.EN_ATTENTE);

        long prescriptionsMois = prescriptionRepository.findAll().stream()
                .filter(p -> p.getDatePrescription() != null &&
                        !p.getDatePrescription().isBefore(debutMois) &&
                        !p.getDatePrescription().isAfter(finMois))
                .count();

        // Top médecins
        List<Medecin> medecins = medecinRepository.findAll();
        List<DashboardStats.MedecinStat> topMedecins = medecins.stream()
                .map(m -> {
                    long totalRdv = rendezVousRepository.countRendezVousByMedecinBetween(
                            m, debutMoisDateTime, finMoisDateTime);
                    return DashboardStats.MedecinStat.builder()
                            .medecinId(m.getId())
                            .nom(m.getUser().getNom())
                            .prenom(m.getUser().getPrenom())
                            .specialite(m.getSpecialite())
                            .totalRdv(totalRdv)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getTotalRdv(), a.getTotalRdv()))
                .limit(5)
                .collect(Collectors.toList());

        // Revenus mensuels (12 derniers mois)
        List<DashboardStats.RevenuMensuel> revenusMensuels = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDate mois = aujourdhui.minusMonths(i);
            LocalDate debut = mois.withDayOfMonth(1);
            LocalDate fin = mois.with(TemporalAdjusters.lastDayOfMonth());
            Double montant = factureRepository.sumMontantTotalBetween(debut, fin);
            long nbFactures = factureRepository.findAll().stream()
                    .filter(f -> !f.getDateFacture().isBefore(debut) && !f.getDateFacture().isAfter(fin))
                    .count();
            revenusMensuels.add(DashboardStats.RevenuMensuel.builder()
                    .mois(mois.getMonthValue())
                    .annee(mois.getYear())
                    .montant(BigDecimal.valueOf(montant != null ? montant : 0))
                    .nombreFactures(nbFactures)
                    .build());
        }

        // RDV par spécialité
        List<DashboardStats.RdvParSpecialite> rdvParSpecialite = medecins.stream()
                .collect(Collectors.groupingBy(
                        Medecin::getSpecialite,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> DashboardStats.RdvParSpecialite.builder()
                        .specialite(e.getKey())
                        .nombre(e.getValue())
                        .build())
                .collect(Collectors.toList());

        // Activités récentes
        List<DashboardStats.ActiviteRecente> activitesRecentes = auditLogRepository
                .findAllByOrderByCreatedAtDesc(org.springframework.data.domain.PageRequest.of(0, 10))
                .stream()
                .map(log -> DashboardStats.ActiviteRecente.builder()
                        .action(log.getAction())
                        .ressource(log.getRessource())
                        .userNom(log.getUser() != null ?
                                log.getUser().getPrenom() + " " + log.getUser().getNom() : "Système")
                        .detail(log.getDetail())
                        .createdAt(log.getCreatedAt() != null ? log.getCreatedAt().toString() : "")
                        .build())
                .collect(Collectors.toList());

        return DashboardStats.builder()
                .totalPatients(totalPatients)
                .totalMedecins(totalMedecins)
                .totalSecretaires(totalSecretaires)
                .rdvAujourdhui(rdvAujourdhui)
                .rdvEnAttente(rdvEnAttente)
                .rdvConfirmes(rdvConfirmes)
                .rdvTermines(rdvTermines)
                .rdvAnnules(rdvAnnules)
                .revenusDuMois(BigDecimal.valueOf(revenusMois != null ? revenusMois : 0))
                .revenusDuJour(BigDecimal.valueOf(revenusJour != null ? revenusJour : 0))
                .facturesImpayees(facturesImpayees)
                .prescriptionsDuMois(prescriptionsMois)
                .topMedecins(topMedecins)
                .revenusMensuels(revenusMensuels)
                .rdvParSpecialite(rdvParSpecialite)
                .activitesRecentes(activitesRecentes)
                .build();
    }

    public DashboardStats getMedecinStats(Medecin medecin) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDateTime debutJour = aujourdhui.atStartOfDay();
        LocalDateTime finJour = aujourdhui.atTime(LocalTime.MAX);

        long rdvAujourdhui = rendezVousRepository.countRendezVousByMedecinBetween(
                medecin, debutJour, finJour);
        long totalPatients = patientRepository.count();

        return DashboardStats.builder()
                .rdvAujourdhui(rdvAujourdhui)
                .totalPatients(totalPatients)
                .build();
    }
}
