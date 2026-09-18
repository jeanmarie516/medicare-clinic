package com.medicare.service;

import com.medicare.dto.FactureDto;
import com.medicare.entity.*;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.pdf.FacturePdfGenerator;
import com.medicare.repository.FactureRepository;
import com.medicare.repository.PatientRepository;
import com.medicare.repository.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FactureService {


    private final FactureRepository factureRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final FacturePdfGenerator pdfGenerator;
    private final AuditService auditService;

    public FactureService(FactureRepository factureRepository, PatientRepository patientRepository, RendezVousRepository rendezVousRepository, FacturePdfGenerator pdfGenerator, AuditService auditService) {
        this.factureRepository = factureRepository;
        this.patientRepository = patientRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.pdfGenerator = pdfGenerator;
        this.auditService = auditService;
    }

    public Page<FactureDto> getAllFactures(int page, int size) {
        return factureRepository.findAll(PageRequest.of(page, size))
                .map(this::toDto);
    }

    public FactureDto getFactureById(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée avec l'id: " + id));
        return toDto(facture);
    }

    public List<FactureDto> getFacturesByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));
        return factureRepository.findByPatientOrderByDateFactureDesc(patient, PageRequest.of(0, 50))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<FactureDto> getFacturesImpayees() {
        return factureRepository.findByStatutPaiement(StatutPaiement.EN_ATTENTE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public FactureDto createFacture(FactureDto dto, User user) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));

        RendezVous rdv = null;
        if (dto.getRdvId() != null) {
            rdv = rendezVousRepository.findById(dto.getRdvId()).orElse(null);
        }

        BigDecimal tva = dto.getTva() != null ? dto.getTva() : BigDecimal.ZERO;
        BigDecimal montantTotal = dto.getMontantTotal() != null ? dto.getMontantTotal() : BigDecimal.ZERO;
        BigDecimal montantPaye = dto.getMontantPaye() != null ? dto.getMontantPaye() : BigDecimal.ZERO;

        StatutPaiement statut;
        if (montantPaye.compareTo(BigDecimal.ZERO) == 0) {
            statut = StatutPaiement.EN_ATTENTE;
        } else if (montantPaye.compareTo(montantTotal) >= 0) {
            statut = StatutPaiement.SOLDE;
        } else {
            statut = StatutPaiement.PARTIELLEMENT_PAYE;
        }

        Facture facture = Facture.builder()
                .patient(patient)
                .rendezVous(rdv)
                .montantTotal(montantTotal)
                .montantPaye(montantPaye)
                .tva(tva)
                .statutPaiement(statut)
                .dateFacture(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .ligneItemsJson(dto.getLigneItemsJson())
                .modePaiement(dto.getModePaiement())
                .notes(dto.getNotes())
                .createdBy(user)
                .build();

        facture = factureRepository.save(facture);

        try {
            String pdfUrl = pdfGenerator.generateFacture(facture);
            facture.setPdfUrl(pdfUrl);
            facture = factureRepository.save(facture);
        } catch (Exception e) {
            // PDF sera généré plus tard
        }

        auditService.log("CREATION", "FACTURE",
                "Création de facture #" + facture.getNumeroFacture() + " pour " + patient.getNom(), user);

        return toDto(facture);
    }

    @Transactional
    public FactureDto marquerPayee(Long id, String modePaiement, User user) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));

        facture.setMontantPaye(facture.getMontantTotal());
        facture.setStatutPaiement(StatutPaiement.SOLDE);
        facture.setModePaiement(modePaiement);
        facture.setDatePaiement(LocalDate.now());
        facture = factureRepository.save(facture);

        auditService.log("PAIEMENT", "FACTURE",
                "Paiement de la facture #" + facture.getNumeroFacture(), user);

        return toDto(facture);
    }

    @Transactional
    public FactureDto enregistrerPaiementPartiel(Long id, BigDecimal montant, String modePaiement, User user) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));

        BigDecimal nouveauPaye = facture.getMontantPaye().add(montant);
        facture.setMontantPaye(nouveauPaye);
        facture.setModePaiement(modePaiement);

        if (nouveauPaye.compareTo(facture.getMontantTotal()) >= 0) {
            facture.setStatutPaiement(StatutPaiement.SOLDE);
            facture.setDatePaiement(LocalDate.now());
        } else {
            facture.setStatutPaiement(StatutPaiement.PARTIELLEMENT_PAYE);
        }

        facture = factureRepository.save(facture);
        return toDto(facture);
    }

    public Facture getFactureEntity(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée avec l'id: " + id));
    }

    @Transactional
    public Facture generatePdfForFacture(Long id) {
        Facture facture = getFactureEntity(id);
        try {
            String pdfUrl = pdfGenerator.generateFacture(facture);
            facture.setPdfUrl(pdfUrl);
            return factureRepository.save(facture);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    public FactureDto toDto(Facture facture) {
        BigDecimal montantRestant = facture.getMontantTotal().subtract(facture.getMontantPaye());
        if (montantRestant.compareTo(BigDecimal.ZERO) < 0) {
            montantRestant = BigDecimal.ZERO;
        }

        return FactureDto.builder()
                .id(facture.getId())
                .numeroFacture(facture.getNumeroFacture())
                .patientId(facture.getPatient().getId())
                .patientNom(facture.getPatient().getNom())
                .patientPrenom(facture.getPatient().getPrenom())
                .rdvId(facture.getRendezVous() != null ? facture.getRendezVous().getId() : null)
                .montantTotal(facture.getMontantTotal())
                .montantPaye(facture.getMontantPaye())
                .montantRestant(montantRestant)
                .tva(facture.getTva())
                .statutPaiement(facture.getStatutPaiement())
                .dateFacture(facture.getDateFacture())
                .dateEcheance(facture.getDateEcheance())
                .pdfUrl(facture.getPdfUrl())
                .ligneItemsJson(facture.getLigneItemsJson())
                .modePaiement(facture.getModePaiement())
                .datePaiement(facture.getDatePaiement())
                .notes(facture.getNotes())
                .createdById(facture.getCreatedBy().getId())
                .createdAt(facture.getCreatedAt())
                .build();
    }
}
