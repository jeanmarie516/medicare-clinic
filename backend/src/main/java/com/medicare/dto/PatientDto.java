package com.medicare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientDto {
    private Long id;
    @NotBlank private String nom;
    @NotBlank private String prenom;
    @NotNull @Past private LocalDate dateNaissance;
    @NotBlank @Pattern(regexp = "^(M|F)$") private String sexe;
    private String telephone; private String email; private String adresse;
    private String groupeSanguin; private String allergies; private String antecedents;
    private String numeroSecuriteSociale; private String profession;
    private String personneContactNom; private String personneContactTelephone;
    private LocalDateTime createdAt; private Long createdById; private String createdByNom;

    public PatientDto() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id=v; }
    public String getNom() { return nom; }
    public void setNom(String v) { this.nom=v; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String v) { this.prenom=v; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate v) { this.dateNaissance=v; }
    public String getSexe() { return sexe; }
    public void setSexe(String v) { this.sexe=v; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String v) { this.telephone=v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email=v; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String v) { this.adresse=v; }
    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String v) { this.groupeSanguin=v; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String v) { this.allergies=v; }
    public String getAntecedents() { return antecedents; }
    public void setAntecedents(String v) { this.antecedents=v; }
    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    public void setNumeroSecuriteSociale(String v) { this.numeroSecuriteSociale=v; }
    public String getProfession() { return profession; }
    public void setProfession(String v) { this.profession=v; }
    public String getPersonneContactNom() { return personneContactNom; }
    public void setPersonneContactNom(String v) { this.personneContactNom=v; }
    public String getPersonneContactTelephone() { return personneContactTelephone; }
    public void setPersonneContactTelephone(String v) { this.personneContactTelephone=v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt=v; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long v) { this.createdById=v; }
    public String getCreatedByNom() { return createdByNom; }
    public void setCreatedByNom(String v) { this.createdByNom=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String nom, prenom; private LocalDate dateNaissance; private String sexe;
        private String telephone, email, adresse, groupeSanguin, allergies, antecedents;
        private String numeroSecuriteSociale, profession, personneContactNom, personneContactTelephone;
        private LocalDateTime createdAt; private Long createdById; private String createdByNom;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder nom(String v) { this.nom=v; return this; }
        public Builder prenom(String v) { this.prenom=v; return this; }
        public Builder dateNaissance(LocalDate v) { this.dateNaissance=v; return this; }
        public Builder sexe(String v) { this.sexe=v; return this; }
        public Builder telephone(String v) { this.telephone=v; return this; }
        public Builder email(String v) { this.email=v; return this; }
        public Builder adresse(String v) { this.adresse=v; return this; }
        public Builder groupeSanguin(String v) { this.groupeSanguin=v; return this; }
        public Builder allergies(String v) { this.allergies=v; return this; }
        public Builder antecedents(String v) { this.antecedents=v; return this; }
        public Builder numeroSecuriteSociale(String v) { this.numeroSecuriteSociale=v; return this; }
        public Builder profession(String v) { this.profession=v; return this; }
        public Builder personneContactNom(String v) { this.personneContactNom=v; return this; }
        public Builder personneContactTelephone(String v) { this.personneContactTelephone=v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt=v; return this; }
        public Builder createdById(Long v) { this.createdById=v; return this; }
        public Builder createdByNom(String v) { this.createdByNom=v; return this; }
        public PatientDto build() {
            PatientDto d = new PatientDto(); d.id=id; d.nom=nom; d.prenom=prenom;
            d.dateNaissance=dateNaissance; d.sexe=sexe; d.telephone=telephone; d.email=email;
            d.adresse=adresse; d.groupeSanguin=groupeSanguin; d.allergies=allergies;
            d.antecedents=antecedents; d.numeroSecuriteSociale=numeroSecuriteSociale;
            d.profession=profession; d.personneContactNom=personneContactNom;
            d.personneContactTelephone=personneContactTelephone; d.createdAt=createdAt;
            d.createdById=createdById; d.createdByNom=createdByNom; return d;
        }
    }
}
