package com.medicare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionDto {
    private Long id;
    @NotNull private Long patientId;
    private String patientNom; private String patientPrenom;
    private String patientTelephone; private String patientEmail;
    private String patientGroupeSanguin; private String patientAllergies;
    private String patientAntecedents; private String patientSexe;
    private String medecinSpecialite;
    @NotNull private Long medecinId;
    private String medecinNom; private String medecinPrenom;
    private Long rdvId;
    private LocalDate datePrescription;
    @NotBlank private String medicamentsJson;
    private List<MedicamentItem> medicaments;
    private String instructions;
    private LocalDate valideJusqua;
    private String diagnostic;
    private String pdfUrl;
    private LocalDateTime createdAt;

    public PrescriptionDto() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id=v; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long v) { this.patientId=v; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String v) { this.patientNom=v; }
    public String getPatientPrenom() { return patientPrenom; }
    public void setPatientPrenom(String v) { this.patientPrenom=v; }
    public String getPatientTelephone() { return patientTelephone; }
    public void setPatientTelephone(String v) { this.patientTelephone=v; }
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String v) { this.patientEmail=v; }
    public String getPatientGroupeSanguin() { return patientGroupeSanguin; }
    public void setPatientGroupeSanguin(String v) { this.patientGroupeSanguin=v; }
    public String getPatientAllergies() { return patientAllergies; }
    public void setPatientAllergies(String v) { this.patientAllergies=v; }
    public String getPatientAntecedents() { return patientAntecedents; }
    public void setPatientAntecedents(String v) { this.patientAntecedents=v; }
    public String getPatientSexe() { return patientSexe; }
    public void setPatientSexe(String v) { this.patientSexe=v; }
    public String getMedecinSpecialite() { return medecinSpecialite; }
    public void setMedecinSpecialite(String v) { this.medecinSpecialite=v; }
    public Long getMedecinId() { return medecinId; }
    public void setMedecinId(Long v) { this.medecinId=v; }
    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String v) { this.medecinNom=v; }
    public String getMedecinPrenom() { return medecinPrenom; }
    public void setMedecinPrenom(String v) { this.medecinPrenom=v; }
    public Long getRdvId() { return rdvId; }
    public void setRdvId(Long v) { this.rdvId=v; }
    public LocalDate getDatePrescription() { return datePrescription; }
    public void setDatePrescription(LocalDate v) { this.datePrescription=v; }
    public String getMedicamentsJson() { return medicamentsJson; }
    public void setMedicamentsJson(String v) { this.medicamentsJson=v; }
    public List<MedicamentItem> getMedicaments() { return medicaments; }
    public void setMedicaments(List<MedicamentItem> v) { this.medicaments=v; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String v) { this.instructions=v; }
    public LocalDate getValideJusqua() { return valideJusqua; }
    public void setValideJusqua(LocalDate v) { this.valideJusqua=v; }
    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String v) { this.diagnostic=v; }
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String v) { this.pdfUrl=v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, patientId, medecinId, rdvId;
        private String patientNom, patientPrenom, patientTelephone, patientEmail;
        private String patientGroupeSanguin, patientAllergies, patientAntecedents, patientSexe;
        private String medecinNom, medecinPrenom, medecinSpecialite;
        private LocalDate datePrescription; private String medicamentsJson;
        private List<MedicamentItem> medicaments; private String instructions;
        private LocalDate valideJusqua; private String diagnostic, pdfUrl;
        private LocalDateTime createdAt;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder patientId(Long v) { this.patientId=v; return this; }
        public Builder patientNom(String v) { this.patientNom=v; return this; }
        public Builder patientPrenom(String v) { this.patientPrenom=v; return this; }
        public Builder patientTelephone(String v) { this.patientTelephone=v; return this; }
        public Builder patientEmail(String v) { this.patientEmail=v; return this; }
        public Builder patientGroupeSanguin(String v) { this.patientGroupeSanguin=v; return this; }
        public Builder patientAllergies(String v) { this.patientAllergies=v; return this; }
        public Builder patientAntecedents(String v) { this.patientAntecedents=v; return this; }
        public Builder patientSexe(String v) { this.patientSexe=v; return this; }
        public Builder medecinSpecialite(String v) { this.medecinSpecialite=v; return this; }
        public Builder medecinId(Long v) { this.medecinId=v; return this; }
        public Builder medecinNom(String v) { this.medecinNom=v; return this; }
        public Builder medecinPrenom(String v) { this.medecinPrenom=v; return this; }
        public Builder rdvId(Long v) { this.rdvId=v; return this; }
        public Builder datePrescription(LocalDate v) { this.datePrescription=v; return this; }
        public Builder medicamentsJson(String v) { this.medicamentsJson=v; return this; }
        public Builder instructions(String v) { this.instructions=v; return this; }
        public Builder valideJusqua(LocalDate v) { this.valideJusqua=v; return this; }
        public Builder diagnostic(String v) { this.diagnostic=v; return this; }
        public Builder pdfUrl(String v) { this.pdfUrl=v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt=v; return this; }
        public PrescriptionDto build() {
            PrescriptionDto d = new PrescriptionDto(); d.id=id; d.patientId=patientId;
            d.patientNom=patientNom; d.patientPrenom=patientPrenom;
            d.patientTelephone=patientTelephone; d.patientEmail=patientEmail;
            d.patientGroupeSanguin=patientGroupeSanguin; d.patientAllergies=patientAllergies;
            d.patientAntecedents=patientAntecedents; d.patientSexe=patientSexe;
            d.medecinId=medecinId;
            d.medecinNom=medecinNom; d.medecinPrenom=medecinPrenom;
            d.medecinSpecialite=medecinSpecialite; d.rdvId=rdvId;
            d.datePrescription=datePrescription; d.medicamentsJson=medicamentsJson;
            d.instructions=instructions; d.valideJusqua=valideJusqua;
            d.diagnostic=diagnostic; d.pdfUrl=pdfUrl; d.createdAt=createdAt; return d;
        }
    }

    public static class MedicamentItem {
        private String nom; private String dosage; private String frequence;
        private String duree; private String instructions;
        public MedicamentItem() {}
        public String getNom() { return nom; }
        public void setNom(String v) { this.nom=v; }
        public String getDosage() { return dosage; }
        public void setDosage(String v) { this.dosage=v; }
        public String getFrequence() { return frequence; }
        public void setFrequence(String v) { this.frequence=v; }
        public String getDuree() { return duree; }
        public void setDuree(String v) { this.duree=v; }
        public String getInstructions() { return instructions; }
        public void setInstructions(String v) { this.instructions=v; }
    }
}
