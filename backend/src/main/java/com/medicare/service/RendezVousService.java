package com.medicare.service;

import com.medicare.dto.RendezVousDto;
import com.medicare.entity.*;
import com.medicare.exception.BadRequestException;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.MedecinRepository;
import com.medicare.repository.PatientRepository;
import com.medicare.repository.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RendezVousService {


    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public RendezVousService(RendezVousRepository rendezVousRepository, PatientRepository patientRepository, MedecinRepository medecinRepository, NotificationService notificationService, AuditService auditService) {
        this.rendezVousRepository = rendezVousRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    public Page<RendezVousDto> getAllRendezVous(int page, int size, String search, Long medecinId) {
        if (medecinId != null) {
            Medecin medecin = medecinRepository.findById(medecinId)
                    .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
            return rendezVousRepository.findByMedecin(medecin, PageRequest.of(page, size))
                    .map(this::toDto);
        }
        if (search != null && !search.isBlank()) {
            return rendezVousRepository.findByPatientNomContainingIgnoreCase(search, PageRequest.of(page, size))
                    .map(this::toDto);
        }
        return rendezVousRepository.findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    public RendezVousDto getRendezVousById(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'id: " + id));
        return toDto(rdv);
    }

    public List<RendezVousDto> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.atTime(LocalTime.MAX);
        return rendezVousRepository.findByMedecinAndDateHeureBetweenOrderByDateHeure(medecin, debut, fin)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<RendezVousDto> getRendezVousBetween(LocalDateTime debut, LocalDateTime fin) {
        return rendezVousRepository.findRendezVousBetween(debut, fin)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<RendezVousDto> getRendezVousWithFilters(LocalDateTime debut, LocalDateTime fin,
                                                        Long medecinId, StatutRendezVous statut) {
        return rendezVousRepository.findWithFilters(debut, fin, medecinId, statut)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RendezVousDto createRendezVous(RendezVousDto dto, User user) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));
        Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));

        LocalDateTime dateHeure = dto.getDateHeure();
        int duree = dto.getDureeMinutes() != null ? dto.getDureeMinutes() : 30;
        LocalDateTime fin = dateHeure.plusMinutes(duree);

        // Vérification conflit médecin
        List<RendezVous> medecinRdvs = rendezVousRepository.findByMedecinAndDateHeureBetweenOrderByDateHeure(
                medecin, dateHeure, fin);
        boolean conflitMedecin = medecinRdvs.stream()
                .anyMatch(r -> r.getStatut() != StatutRendezVous.ANNULE && r.getStatut() != StatutRendezVous.ABSENT);
        if (conflitMedecin) {
            throw new BadRequestException("Le médecin a déjà un rendez-vous à ce créneau horaire");
        }

        // Vérification conflit patient (même jour)
        LocalDateTime debutJour = dateHeure.toLocalDate().atStartOfDay();
        LocalDateTime finJour = dateHeure.toLocalDate().atTime(LocalTime.MAX);
        List<RendezVous> patientRdvs = rendezVousRepository.findByPatientAndDateHeureBetweenOrderByDateHeure(
                patient, debutJour, finJour);
        boolean aDejaRdv = patientRdvs.stream()
                .anyMatch(r -> r.getStatut() != StatutRendezVous.ANNULE && r.getStatut() != StatutRendezVous.ABSENT);
        if (aDejaRdv) {
            throw new BadRequestException("Le patient a déjà un rendez-vous ce jour-là");
        }

        RendezVous rendezVous = RendezVous.builder()
                .patient(patient)
                .medecin(medecin)
                .dateHeure(dateHeure)
                .dureeMinutes(duree)
                .statut(dto.getStatut() != null ? dto.getStatut() : StatutRendezVous.EN_ATTENTE)
                .motif(dto.getMotif())
                .notes(dto.getNotes())
                .salle(dto.getSalle())
                .createdBy(user)
                .build();

        rendezVous = rendezVousRepository.save(rendezVous);

        notificationService.createNotification(
                medecin.getUser(),
                "NOUVEAU_RDV",
                "Nouveau rendez-vous",
                "Un rendez-vous a été programmé avec " + patient.getPrenom() + " " + patient.getNom() +
                        " le " + dateHeure.toLocalDate() + " à " + dateHeure.toLocalTime(),
                "/rendez-vous/" + rendezVous.getId()
        );

        auditService.log("CREATION", "RENDEZ_VOUS",
                "Création du RDV pour " + patient.getNom() + " avec Dr " + medecin.getUser().getNom(), user);

        return toDto(rendezVous);
    }

    @Transactional
    public RendezVousDto updateRendezVous(Long id, RendezVousDto dto, User user) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé"));

        // Patient, médecin, date, durée pour la vérification de conflit
        Patient patient = rdv.getPatient();
        if (dto.getPatientId() != null && !dto.getPatientId().equals(rdv.getPatient().getId())) {
            patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));
        }

        LocalDateTime dateHeure = dto.getDateHeure() != null ? dto.getDateHeure() : rdv.getDateHeure();
        int duree = dto.getDureeMinutes() != null ? dto.getDureeMinutes() : rdv.getDureeMinutes();
        Medecin medecin = rdv.getMedecin();

        if (dto.getMedecinId() != null && !dto.getMedecinId().equals(rdv.getMedecin().getId())) {
            medecin = medecinRepository.findById(dto.getMedecinId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
        }

        LocalDateTime fin = dateHeure.plusMinutes(duree);
        List<RendezVous> medecinRdvs = rendezVousRepository.findConflictsForUpdate(medecin, dateHeure, fin, id);
        boolean conflitMedecin = medecinRdvs.stream()
                .anyMatch(r -> r.getStatut() != StatutRendezVous.ANNULE && r.getStatut() != StatutRendezVous.ABSENT);
        if (conflitMedecin) {
            throw new BadRequestException("Le médecin a déjà un rendez-vous à ce créneau horaire");
        }

        // Vérification conflit patient (même jour, hors RDV courant)
        LocalDateTime debutJour = dateHeure.toLocalDate().atStartOfDay();
        LocalDateTime finJour = dateHeure.toLocalDate().atTime(LocalTime.MAX);
        List<RendezVous> patientRdvs = rendezVousRepository.findByPatientAndDateHeureBetweenOrderByDateHeure(
                patient, debutJour, finJour);
        boolean aDejaRdv = patientRdvs.stream()
                .anyMatch(r -> !r.getId().equals(id)
                        && r.getStatut() != StatutRendezVous.ANNULE
                        && r.getStatut() != StatutRendezVous.ABSENT);
        if (aDejaRdv) {
            throw new BadRequestException("Le patient a déjà un rendez-vous ce jour-là");
        }

        rdv.setPatient(patient);
        rdv.setMedecin(medecin);
        rdv.setDateHeure(dateHeure);
        rdv.setDureeMinutes(duree);
        if (dto.getStatut() != null) {
            rdv.setStatut(dto.getStatut());
        }
        if (dto.getMotif() != null) {
            rdv.setMotif(dto.getMotif());
        }
        if (dto.getNotes() != null) {
            rdv.setNotes(dto.getNotes());
        }
        if (dto.getSalle() != null) {
            rdv.setSalle(dto.getSalle());
        }

        rdv = rendezVousRepository.save(rdv);
        auditService.log("MODIFICATION", "RENDEZ_VOUS",
                "Modification du RDV #" + rdv.getId(), user);
        return toDto(rdv);
    }

    @Transactional
    public RendezVousDto annulerRendezVous(Long id, User user) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé"));

        if (user.getRole() != Role.ADMIN) {
            LocalDateTime now = LocalDateTime.now();
            if (rdv.getDateHeure().minusHours(24).isBefore(now)) {
                throw new BadRequestException(
                        "L'annulation n'est possible que jusqu'à 24h avant le rendez-vous. " +
                        "Veuillez contacter l'administrateur.");
            }
        }

        rdv.setStatut(StatutRendezVous.ANNULE);
        rdv = rendezVousRepository.save(rdv);

        notificationService.createNotification(
                rdv.getMedecin().getUser(),
                "ANNULATION_RDV",
                "Rendez-vous annulé",
                "Le rendez-vous du " + rdv.getDateHeure().toLocalDate() +
                        " avec " + rdv.getPatient().getPrenom() + " " + rdv.getPatient().getNom() + " a été annulé",
                "/rendez-vous/" + rdv.getId()
        );

        auditService.log("ANNULATION", "RENDEZ_VOUS",
                "Annulation du RDV #" + rdv.getId() + " par " + user.getEmail(), user);
        return toDto(rdv);
    }

    @Transactional
    public RendezVousDto changerStatut(Long id, StatutRendezVous nouveauStatut, User user) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé"));
        rdv.setStatut(nouveauStatut);
        rdv = rendezVousRepository.save(rdv);
        auditService.log("CHANGEMENT_STATUT", "RENDEZ_VOUS",
                "Changement statut RDV #" + rdv.getId() + " vers " + nouveauStatut, user);
        return toDto(rdv);
    }

    public RendezVousDto toDto(RendezVous rdv) {
        return RendezVousDto.builder()
                .id(rdv.getId())
                .patientId(rdv.getPatient().getId())
                .patientNom(rdv.getPatient().getNom())
                .patientPrenom(rdv.getPatient().getPrenom())
                .medecinId(rdv.getMedecin().getId())
                .medecinNom(rdv.getMedecin().getUser().getNom())
                .medecinPrenom(rdv.getMedecin().getUser().getPrenom())
                .medecinSpecialite(rdv.getMedecin().getSpecialite())
                .dateHeure(rdv.getDateHeure())
                .dureeMinutes(rdv.getDureeMinutes())
                .statut(rdv.getStatut())
                .motif(rdv.getMotif())
                .notes(rdv.getNotes())
                .salle(rdv.getSalle())
                .createdById(rdv.getCreatedBy().getId())
                .createdAt(rdv.getCreatedAt())
                .build();
    }
}
