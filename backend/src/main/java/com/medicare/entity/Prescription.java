package com.medicare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "patient_id", nullable = false) private Patient patient;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "medecin_id", nullable = false) private Medecin medecin;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "rdv_id") private RendezVous rendezVous;
    @Column(name = "date_prescription", nullable = false) private LocalDate datePrescription;
    @Column(name = "medicaments_json", columnDefinition = "TEXT", nullable = false) private String medicamentsJson;
    @Column(columnDefinition = "TEXT") private String instructions;
    @Column(name = "valide_jusqua") private LocalDate valideJusqua;
    @Column(name = "pdf_url") private String pdfUrl;
    @Column(columnDefinition = "TEXT") private String diagnostic;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    public Prescription() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.datePrescription == null) this.datePrescription = LocalDate.now();
    }

    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Medecin getMedecin() { return medecin; }
    public RendezVous getRendezVous() { return rendezVous; }
    public LocalDate getDatePrescription() { return datePrescription; }
    public String getMedicamentsJson() { return medicamentsJson; }
    public String getInstructions() { return instructions; }
    public LocalDate getValideJusqua() { return valideJusqua; }
    public String getPdfUrl() { return pdfUrl; }
    public String getDiagnostic() { return diagnostic; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long v) { this.id = v; }
    public void setPatient(Patient v) { this.patient = v; }
    public void setMedecin(Medecin v) { this.medecin = v; }
    public void setRendezVous(RendezVous v) { this.rendezVous = v; }
    public void setDatePrescription(LocalDate v) { this.datePrescription = v; }
    public void setMedicamentsJson(String v) { this.medicamentsJson = v; }
    public void setInstructions(String v) { this.instructions = v; }
    public void setValideJusqua(LocalDate v) { this.valideJusqua = v; }
    public void setPdfUrl(String v) { this.pdfUrl = v; }
    public void setDiagnostic(String v) { this.diagnostic = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private Patient patient; private Medecin medecin;
        private RendezVous rendezVous; private LocalDate datePrescription;
        private String medicamentsJson, instructions; private LocalDate valideJusqua;
        private String pdfUrl, diagnostic;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder patient(Patient v) { this.patient=v; return this; }
        public Builder medecin(Medecin v) { this.medecin=v; return this; }
        public Builder rendezVous(RendezVous v) { this.rendezVous=v; return this; }
        public Builder datePrescription(LocalDate v) { this.datePrescription=v; return this; }
        public Builder medicamentsJson(String v) { this.medicamentsJson=v; return this; }
        public Builder instructions(String v) { this.instructions=v; return this; }
        public Builder valideJusqua(LocalDate v) { this.valideJusqua=v; return this; }
        public Builder pdfUrl(String v) { this.pdfUrl=v; return this; }
        public Builder diagnostic(String v) { this.diagnostic=v; return this; }
        public Prescription build() {
            Prescription p = new Prescription(); p.id=id; p.patient=patient; p.medecin=medecin;
            p.rendezVous=rendezVous; p.datePrescription=datePrescription;
            p.medicamentsJson=medicamentsJson; p.instructions=instructions;
            p.valideJusqua=valideJusqua; p.pdfUrl=pdfUrl; p.diagnostic=diagnostic; return p;
        }
    }
}
