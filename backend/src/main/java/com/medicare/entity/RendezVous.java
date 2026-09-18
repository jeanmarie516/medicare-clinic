package com.medicare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
public class RendezVous {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "patient_id", nullable = false) private Patient patient;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "medecin_id", nullable = false) private Medecin medecin;
    @Column(name = "date_heure", nullable = false) private LocalDateTime dateHeure;
    @Column(name = "duree_minutes", nullable = false) private Integer dureeMinutes = 30;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private StatutRendezVous statut = StatutRendezVous.EN_ATTENTE;
    @Column(columnDefinition = "TEXT") private String motif;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(length = 50) private String salle;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by", nullable = false) private User createdBy;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    public RendezVous() {}

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    // Getters
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Medecin getMedecin() { return medecin; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public StatutRendezVous getStatut() { return statut; }
    public String getMotif() { return motif; }
    public String getNotes() { return notes; }
    public String getSalle() { return salle; }
    public User getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long v) { this.id = v; }
    public void setPatient(Patient v) { this.patient = v; }
    public void setMedecin(Medecin v) { this.medecin = v; }
    public void setDateHeure(LocalDateTime v) { this.dateHeure = v; }
    public void setDureeMinutes(Integer v) { this.dureeMinutes = v; }
    public void setStatut(StatutRendezVous v) { this.statut = v; }
    public void setMotif(String v) { this.motif = v; }
    public void setNotes(String v) { this.notes = v; }
    public void setSalle(String v) { this.salle = v; }
    public void setCreatedBy(User v) { this.createdBy = v; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private Patient patient; private Medecin medecin;
        private LocalDateTime dateHeure; private Integer dureeMinutes = 30;
        private StatutRendezVous statut = StatutRendezVous.EN_ATTENTE;
        private String motif, notes, salle; private User createdBy;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder patient(Patient v) { this.patient=v; return this; }
        public Builder medecin(Medecin v) { this.medecin=v; return this; }
        public Builder dateHeure(LocalDateTime v) { this.dateHeure=v; return this; }
        public Builder dureeMinutes(Integer v) { this.dureeMinutes=v; return this; }
        public Builder statut(StatutRendezVous v) { this.statut=v; return this; }
        public Builder motif(String v) { this.motif=v; return this; }
        public Builder notes(String v) { this.notes=v; return this; }
        public Builder salle(String v) { this.salle=v; return this; }
        public Builder createdBy(User v) { this.createdBy=v; return this; }
        public RendezVous build() {
            RendezVous r = new RendezVous(); r.id=id; r.patient=patient; r.medecin=medecin;
            r.dateHeure=dateHeure; r.dureeMinutes=dureeMinutes; r.statut=statut;
            r.motif=motif; r.notes=notes; r.salle=salle; r.createdBy=createdBy; return r;
        }
    }
}
