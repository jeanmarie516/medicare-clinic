package com.medicare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture", length = 50, unique = true)
    private String numeroFacture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rdv_id")
    private RendezVous rendezVous;

    @Column(name = "montant_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal montantTotal;

    @Column(name = "montant_paye", precision = 10, scale = 2, nullable = false)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(name = "tva", precision = 10, scale = 2)
    private BigDecimal tva;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false, length = 30)
    private StatutPaiement statutPaiement = StatutPaiement.EN_ATTENTE;

    @Column(name = "date_facture", nullable = false)
    private LocalDate dateFacture;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "ligne_items_json", columnDefinition = "TEXT")
    private String ligneItemsJson;

    @Column(name = "mode_paiement", length = 30)
    private String modePaiement;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Facture() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.dateFacture == null) this.dateFacture = LocalDate.now();
        if (this.numeroFacture == null) this.numeroFacture = "FACT-" + System.currentTimeMillis();
    }

    // Getters
    public Long getId() { return id; }
    public String getNumeroFacture() { return numeroFacture; }
    public Patient getPatient() { return patient; }
    public RendezVous getRendezVous() { return rendezVous; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public BigDecimal getMontantPaye() { return montantPaye; }
    public BigDecimal getTva() { return tva; }
    public StatutPaiement getStatutPaiement() { return statutPaiement; }
    public LocalDate getDateFacture() { return dateFacture; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public String getPdfUrl() { return pdfUrl; }
    public String getLigneItemsJson() { return ligneItemsJson; }
    public String getModePaiement() { return modePaiement; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public String getNotes() { return notes; }
    public User getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setRendezVous(RendezVous rendezVous) { this.rendezVous = rendezVous; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    public void setMontantPaye(BigDecimal montantPaye) { this.montantPaye = montantPaye; }
    public void setTva(BigDecimal tva) { this.tva = tva; }
    public void setStatutPaiement(StatutPaiement statutPaiement) { this.statutPaiement = statutPaiement; }
    public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public void setLigneItemsJson(String ligneItemsJson) { this.ligneItemsJson = ligneItemsJson; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private Patient patient; private RendezVous rendezVous;
        private BigDecimal montantTotal; private BigDecimal montantPaye = BigDecimal.ZERO;
        private BigDecimal tva; private StatutPaiement statutPaiement = StatutPaiement.EN_ATTENTE;
        private LocalDate dateFacture; private LocalDate dateEcheance; private String pdfUrl;
        private String ligneItemsJson; private String modePaiement; private LocalDate datePaiement;
        private String notes; private User createdBy; private LocalDateTime createdAt;
        private String numeroFacture;

        Builder() {}

        public Builder id(Long v) { this.id = v; return this; }
        public Builder numeroFacture(String v) { this.numeroFacture = v; return this; }
        public Builder patient(Patient v) { this.patient = v; return this; }
        public Builder rendezVous(RendezVous v) { this.rendezVous = v; return this; }
        public Builder montantTotal(BigDecimal v) { this.montantTotal = v; return this; }
        public Builder montantPaye(BigDecimal v) { this.montantPaye = v; return this; }
        public Builder tva(BigDecimal v) { this.tva = v; return this; }
        public Builder statutPaiement(StatutPaiement v) { this.statutPaiement = v; return this; }
        public Builder dateFacture(LocalDate v) { this.dateFacture = v; return this; }
        public Builder dateEcheance(LocalDate v) { this.dateEcheance = v; return this; }
        public Builder pdfUrl(String v) { this.pdfUrl = v; return this; }
        public Builder ligneItemsJson(String v) { this.ligneItemsJson = v; return this; }
        public Builder modePaiement(String v) { this.modePaiement = v; return this; }
        public Builder datePaiement(LocalDate v) { this.datePaiement = v; return this; }
        public Builder notes(String v) { this.notes = v; return this; }
        public Builder createdBy(User v) { this.createdBy = v; return this; }

        public Facture build() {
            Facture f = new Facture();
            f.id = this.id; f.numeroFacture = this.numeroFacture; f.patient = this.patient;
            f.rendezVous = this.rendezVous; f.montantTotal = this.montantTotal;
            f.montantPaye = this.montantPaye; f.tva = this.tva;
            f.statutPaiement = this.statutPaiement; f.dateFacture = this.dateFacture;
            f.dateEcheance = this.dateEcheance; f.pdfUrl = this.pdfUrl;
            f.ligneItemsJson = this.ligneItemsJson; f.modePaiement = this.modePaiement;
            f.datePaiement = this.datePaiement; f.notes = this.notes; f.createdBy = this.createdBy;
            return f;
        }
    }
}
