package com.medicare.dto;

import com.medicare.entity.StatutPaiement;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FactureDto {
    private Long id; private String numeroFacture;
    @NotNull private Long patientId;
    private String patientNom; private String patientPrenom;
    private Long rdvId;
    private BigDecimal montantTotal; private BigDecimal montantPaye; private BigDecimal tva; private BigDecimal montantRestant;
    private StatutPaiement statutPaiement;
    private LocalDate dateFacture; private LocalDate dateEcheance;
    private String pdfUrl; private String ligneItemsJson;
    private List<LigneItem> ligneItems;
    private String modePaiement; private LocalDate datePaiement;
    private String notes; private Long createdById;
    private LocalDateTime createdAt;

    public FactureDto() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id=v; }
    public String getNumeroFacture() { return numeroFacture; }
    public void setNumeroFacture(String v) { this.numeroFacture=v; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long v) { this.patientId=v; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String v) { this.patientNom=v; }
    public String getPatientPrenom() { return patientPrenom; }
    public void setPatientPrenom(String v) { this.patientPrenom=v; }
    public Long getRdvId() { return rdvId; }
    public void setRdvId(Long v) { this.rdvId=v; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal v) { this.montantTotal=v; }
    public BigDecimal getMontantPaye() { return montantPaye; }
    public void setMontantPaye(BigDecimal v) { this.montantPaye=v; }
    public BigDecimal getTva() { return tva; }
    public void setTva(BigDecimal v) { this.tva=v; }
    public BigDecimal getMontantRestant() { return montantRestant; }
    public void setMontantRestant(BigDecimal v) { this.montantRestant=v; }
    public StatutPaiement getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(StatutPaiement v) { this.statutPaiement=v; }
    public LocalDate getDateFacture() { return dateFacture; }
    public void setDateFacture(LocalDate v) { this.dateFacture=v; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate v) { this.dateEcheance=v; }
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String v) { this.pdfUrl=v; }
    public String getLigneItemsJson() { return ligneItemsJson; }
    public void setLigneItemsJson(String v) { this.ligneItemsJson=v; }
    public List<LigneItem> getLigneItems() { return ligneItems; }
    public void setLigneItems(List<LigneItem> v) { this.ligneItems=v; }
    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String v) { this.modePaiement=v; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate v) { this.datePaiement=v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes=v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById=v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, patientId, rdvId, createdById;
        private String numeroFacture, patientNom, patientPrenom;
        private BigDecimal montantTotal, montantPaye, tva, montantRestant;
        private StatutPaiement statutPaiement;
        private LocalDate dateFacture, dateEcheance, datePaiement;
        private String pdfUrl, ligneItemsJson, modePaiement, notes;
        private LocalDateTime createdAt;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder numeroFacture(String v) { this.numeroFacture=v; return this; }
        public Builder patientId(Long v) { this.patientId=v; return this; }
        public Builder patientNom(String v) { this.patientNom=v; return this; }
        public Builder patientPrenom(String v) { this.patientPrenom=v; return this; }
        public Builder rdvId(Long v) { this.rdvId=v; return this; }
        public Builder montantTotal(BigDecimal v) { this.montantTotal=v; return this; }
        public Builder montantPaye(BigDecimal v) { this.montantPaye=v; return this; }
        public Builder tva(BigDecimal v) { this.tva=v; return this; }
        public Builder montantRestant(BigDecimal v) { this.montantRestant=v; return this; }
        public Builder statutPaiement(StatutPaiement v) { this.statutPaiement=v; return this; }
        public Builder dateFacture(LocalDate v) { this.dateFacture=v; return this; }
        public Builder dateEcheance(LocalDate v) { this.dateEcheance=v; return this; }
        public Builder pdfUrl(String v) { this.pdfUrl=v; return this; }
        public Builder ligneItemsJson(String v) { this.ligneItemsJson=v; return this; }
        public Builder modePaiement(String v) { this.modePaiement=v; return this; }
        public Builder datePaiement(LocalDate v) { this.datePaiement=v; return this; }
        public Builder notes(String v) { this.notes=v; return this; }
        public Builder createdById(Long v) { this.createdById=v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt=v; return this; }
        public FactureDto build() {
            FactureDto d = new FactureDto(); d.id=id; d.numeroFacture=numeroFacture;
            d.patientId=patientId; d.patientNom=patientNom; d.patientPrenom=patientPrenom;
            d.rdvId=rdvId; d.montantTotal=montantTotal; d.montantPaye=montantPaye;
            d.tva=tva; d.montantRestant=montantRestant; d.statutPaiement=statutPaiement;
            d.dateFacture=dateFacture; d.dateEcheance=dateEcheance; d.pdfUrl=pdfUrl;
            d.ligneItemsJson=ligneItemsJson; d.modePaiement=modePaiement;
            d.datePaiement=datePaiement; d.notes=notes; d.createdById=createdById;
            d.createdAt=createdAt; return d;
        }
    }

    public static class LigneItem {
        private String description; private Integer quantite;
        private BigDecimal prixUnitaire; private BigDecimal montant;
        public LigneItem() {}
        public String getDescription() { return description; }
        public void setDescription(String v) { this.description=v; }
        public Integer getQuantite() { return quantite; }
        public void setQuantite(Integer v) { this.quantite=v; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal v) { this.prixUnitaire=v; }
        public BigDecimal getMontant() { return montant; }
        public void setMontant(BigDecimal v) { this.montant=v; }
    }
}
