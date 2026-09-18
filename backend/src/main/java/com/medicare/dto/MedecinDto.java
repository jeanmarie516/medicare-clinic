package com.medicare.dto;

import java.math.BigDecimal;

public class MedecinDto {
    private Long id; private Long userId; private String nom; private String prenom;
    private String email; private String telephone; private String photoUrl;
    private String specialite; private String numeroOrdre; private Boolean disponible;
    private BigDecimal tarifConsultation; private String biographie; private Integer anneesExperience;

    public MedecinDto() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id=v; }
    public Long getUserId() { return userId; }
    public void setUserId(Long v) { this.userId=v; }
    public String getNom() { return nom; }
    public void setNom(String v) { this.nom=v; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String v) { this.prenom=v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email=v; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String v) { this.telephone=v; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String v) { this.photoUrl=v; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String v) { this.specialite=v; }
    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String v) { this.numeroOrdre=v; }
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean v) { this.disponible=v; }
    public BigDecimal getTarifConsultation() { return tarifConsultation; }
    public void setTarifConsultation(BigDecimal v) { this.tarifConsultation=v; }
    public String getBiographie() { return biographie; }
    public void setBiographie(String v) { this.biographie=v; }
    public Integer getAnneesExperience() { return anneesExperience; }
    public void setAnneesExperience(Integer v) { this.anneesExperience=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, userId; private String nom, prenom, email, telephone, photoUrl;
        private String specialite, numeroOrdre; private Boolean disponible;
        private BigDecimal tarifConsultation; private String biographie; private Integer anneesExperience;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder userId(Long v) { this.userId=v; return this; }
        public Builder nom(String v) { this.nom=v; return this; }
        public Builder prenom(String v) { this.prenom=v; return this; }
        public Builder email(String v) { this.email=v; return this; }
        public Builder telephone(String v) { this.telephone=v; return this; }
        public Builder photoUrl(String v) { this.photoUrl=v; return this; }
        public Builder specialite(String v) { this.specialite=v; return this; }
        public Builder numeroOrdre(String v) { this.numeroOrdre=v; return this; }
        public Builder disponible(Boolean v) { this.disponible=v; return this; }
        public Builder tarifConsultation(BigDecimal v) { this.tarifConsultation=v; return this; }
        public Builder biographie(String v) { this.biographie=v; return this; }
        public Builder anneesExperience(Integer v) { this.anneesExperience=v; return this; }
        public MedecinDto build() {
            MedecinDto d = new MedecinDto(); d.id=id; d.userId=userId; d.nom=nom; d.prenom=prenom;
            d.email=email; d.telephone=telephone; d.photoUrl=photoUrl; d.specialite=specialite;
            d.numeroOrdre=numeroOrdre; d.disponible=disponible; d.tarifConsultation=tarifConsultation;
            d.biographie=biographie; d.anneesExperience=anneesExperience; return d;
        }
    }
}
