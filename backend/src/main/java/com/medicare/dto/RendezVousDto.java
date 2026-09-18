package com.medicare.dto;

import com.medicare.entity.StatutRendezVous;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class RendezVousDto {
    private Long id;
    @NotNull private Long patientId;
    private String patientNom; private String patientPrenom;
    @NotNull private Long medecinId;
    private String medecinNom; private String medecinPrenom; private String medecinSpecialite;
    @NotNull @Future private LocalDateTime dateHeure;
    private Integer dureeMinutes;
    private StatutRendezVous statut;
    private String motif; private String notes; private String salle;
    private Long createdById;
    private LocalDateTime createdAt;

    public RendezVousDto() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id=v; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long v) { this.patientId=v; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String v) { this.patientNom=v; }
    public String getPatientPrenom() { return patientPrenom; }
    public void setPatientPrenom(String v) { this.patientPrenom=v; }
    public Long getMedecinId() { return medecinId; }
    public void setMedecinId(Long v) { this.medecinId=v; }
    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String v) { this.medecinNom=v; }
    public String getMedecinPrenom() { return medecinPrenom; }
    public void setMedecinPrenom(String v) { this.medecinPrenom=v; }
    public String getMedecinSpecialite() { return medecinSpecialite; }
    public void setMedecinSpecialite(String v) { this.medecinSpecialite=v; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime v) { this.dateHeure=v; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer v) { this.dureeMinutes=v; }
    public StatutRendezVous getStatut() { return statut; }
    public void setStatut(StatutRendezVous v) { this.statut=v; }
    public String getMotif() { return motif; }
    public void setMotif(String v) { this.motif=v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes=v; }
    public String getSalle() { return salle; }
    public void setSalle(String v) { this.salle=v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById=v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, patientId, medecinId, createdById;
        private String patientNom, patientPrenom, medecinNom, medecinPrenom, medecinSpecialite;
        private LocalDateTime dateHeure; private Integer dureeMinutes;
        private StatutRendezVous statut; private String motif, notes, salle;
        private LocalDateTime createdAt;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder patientId(Long v) { this.patientId=v; return this; }
        public Builder patientNom(String v) { this.patientNom=v; return this; }
        public Builder patientPrenom(String v) { this.patientPrenom=v; return this; }
        public Builder medecinId(Long v) { this.medecinId=v; return this; }
        public Builder medecinNom(String v) { this.medecinNom=v; return this; }
        public Builder medecinPrenom(String v) { this.medecinPrenom=v; return this; }
        public Builder medecinSpecialite(String v) { this.medecinSpecialite=v; return this; }
        public Builder dateHeure(LocalDateTime v) { this.dateHeure=v; return this; }
        public Builder dureeMinutes(Integer v) { this.dureeMinutes=v; return this; }
        public Builder statut(StatutRendezVous v) { this.statut=v; return this; }
        public Builder motif(String v) { this.motif=v; return this; }
        public Builder notes(String v) { this.notes=v; return this; }
        public Builder salle(String v) { this.salle=v; return this; }
        public Builder createdById(Long v) { this.createdById=v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt=v; return this; }
        public RendezVousDto build() {
            RendezVousDto d = new RendezVousDto(); d.id=id; d.patientId=patientId;
            d.patientNom=patientNom; d.patientPrenom=patientPrenom; d.medecinId=medecinId;
            d.medecinNom=medecinNom; d.medecinPrenom=medecinPrenom;
            d.medecinSpecialite=medecinSpecialite; d.dateHeure=dateHeure;
            d.dureeMinutes=dureeMinutes; d.statut=statut; d.motif=motif; d.notes=notes;
            d.salle=salle; d.createdById=createdById; d.createdAt=createdAt; return d;
        }
    }
}
