package com.medicare.service;

import com.medicare.dto.PrescriptionDto;
import com.medicare.entity.*;
import com.medicare.exception.BadRequestException;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.pdf.PrescriptionPdfGenerator;
import com.medicare.repository.MedecinRepository;
import com.medicare.repository.PatientRepository;
import com.medicare.repository.PrescriptionRepository;
import com.medicare.repository.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {


    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezVousRepository;
    private final PrescriptionPdfGenerator pdfGenerator;
    private final AuditService auditService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, PatientRepository patientRepository, MedecinRepository medecinRepository, RendezVousRepository rendezVousRepository, PrescriptionPdfGenerator pdfGenerator, AuditService auditService) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.pdfGenerator = pdfGenerator;
        this.auditService = auditService;
    }

    public Page<PrescriptionDto> getAllPrescriptions(int page, int size) {
        return prescriptionRepository.findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    public PrescriptionDto getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription non trouvée avec l'id: " + id));
        return toDto(prescription);
    }

    public List<PrescriptionDto> getPrescriptionsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));
        return prescriptionRepository.findByPatientOrderByDatePrescriptionDesc(patient)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrescriptionDto createPrescription(PrescriptionDto dto, User user) {
        if (user.getRole() != Role.MEDECIN && user.getRole() != Role.ADMIN) {
            throw new BadRequestException("Seul un médecin peut créer une prescription");
        }

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));

        Medecin medecin;
        if (user.getRole() == Role.MEDECIN) {
            medecin = medecinRepository.findAll().stream()
                    .filter(m -> m.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Profil médecin non trouvé"));
        } else {
            medecin = medecinRepository.findById(dto.getMedecinId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
        }

        RendezVous rdv = null;
        if (dto.getRdvId() != null) {
            rdv = rendezVousRepository.findById(dto.getRdvId())
                    .orElse(null);
        }

        if (dto.getMedicamentsJson() == null || dto.getMedicamentsJson().isBlank()) {
            throw new BadRequestException("La prescription doit contenir au moins un médicament");
        }

        Prescription prescription = Prescription.builder()
                .patient(patient)
                .medecin(medecin)
                .rendezVous(rdv)
                .datePrescription(dto.getDatePrescription())
                .medicamentsJson(dto.getMedicamentsJson())
                .instructions(dto.getInstructions())
                .valideJusqua(dto.getValideJusqua())
                .diagnostic(dto.getDiagnostic())
                .build();

        prescription = prescriptionRepository.save(prescription);

        // Génération du PDF
        try {
            String pdfUrl = pdfGenerator.generatePrescription(prescription);
            prescription.setPdfUrl(pdfUrl);
            prescription = prescriptionRepository.save(prescription);
        } catch (Exception e) {
            // Le PDF sera généré plus tard
        }

        auditService.log("CREATION", "PRESCRIPTION",
                "Création de prescription pour " + patient.getNom() + " " + patient.getPrenom(), user);

        return toDto(prescription);
    }

    public Prescription getPrescriptionEntity(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription non trouvée avec l'id: " + id));
    }

    @Transactional
    public Prescription generatePdfForPrescription(Long id) {
        Prescription prescription = getPrescriptionEntity(id);
        try {
            String pdfUrl = pdfGenerator.generatePrescription(prescription);
            prescription.setPdfUrl(pdfUrl);
            return prescriptionRepository.save(prescription);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    public PrescriptionDto toDto(Prescription prescription) {
        return PrescriptionDto.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatient().getId())
                .patientNom(prescription.getPatient().getNom())
                .patientPrenom(prescription.getPatient().getPrenom())
                .patientTelephone(prescription.getPatient().getTelephone())
                .patientEmail(prescription.getPatient().getEmail())
                .patientGroupeSanguin(prescription.getPatient().getGroupeSanguin())
                .patientAllergies(prescription.getPatient().getAllergies())
                .patientAntecedents(prescription.getPatient().getAntecedents())
                .patientSexe(prescription.getPatient().getSexe())
                .medecinId(prescription.getMedecin().getId())
                .medecinNom(prescription.getMedecin().getUser().getNom())
                .medecinPrenom(prescription.getMedecin().getUser().getPrenom())
                .medecinSpecialite(prescription.getMedecin().getSpecialite())
                .rdvId(prescription.getRendezVous() != null ? prescription.getRendezVous().getId() : null)
                .datePrescription(prescription.getDatePrescription())
                .medicamentsJson(prescription.getMedicamentsJson())
                .instructions(prescription.getInstructions())
                .valideJusqua(prescription.getValideJusqua())
                .diagnostic(prescription.getDiagnostic())
                .pdfUrl(prescription.getPdfUrl())
                .createdAt(prescription.getCreatedAt())
                .build();
    }
}
