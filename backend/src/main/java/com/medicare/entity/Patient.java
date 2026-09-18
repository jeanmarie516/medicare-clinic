package com.medicare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50) private String nom;
    @Column(nullable = false, length = 50) private String prenom;
    @Column(name = "date_naissance", nullable = false) private LocalDate dateNaissance;
    @Column(nullable = false, length = 10) private String sexe;
    @Column(length = 20) private String telephone;
    @Column(length = 100) private String email;
    @Column(columnDefinition = "TEXT") private String adresse;
    @Column(name = "groupe_sanguin", length = 5) private String groupeSanguin;
    @Column(columnDefinition = "TEXT") private String allergies;
    @Column(columnDefinition = "TEXT") private String antecedents;
    @Column(name = "numero_securite_sociale", length = 50) private String numeroSecuriteSociale;
    @Column(length = 100) private String profession;
    @Column(name = "personne_contact_nom", length = 100) private String personneContactNom;
    @Column(name = "personne_contact_telephone", length = 20) private String personneContactTelephone;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by", nullable = false) private User createdBy;

    public Patient() {}

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getSexe() { return sexe; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getAdresse() { return adresse; }
    public String getGroupeSanguin() { return groupeSanguin; }
    public String getAllergies() { return allergies; }
    public String getAntecedents() { return antecedents; }
    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    public String getProfession() { return profession; }
    public String getPersonneContactNom() { return personneContactNom; }
    public String getPersonneContactTelephone() { return personneContactTelephone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getCreatedBy() { return createdBy; }

    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateNaissance(LocalDate d) { this.dateNaissance = d; }
    public void setSexe(String s) { this.sexe = s; }
    public void setTelephone(String t) { this.telephone = t; }
    public void setEmail(String e) { this.email = e; }
    public void setAdresse(String a) { this.adresse = a; }
    public void setGroupeSanguin(String g) { this.groupeSanguin = g; }
    public void setAllergies(String a) { this.allergies = a; }
    public void setAntecedents(String a) { this.antecedents = a; }
    public void setNumeroSecuriteSociale(String n) { this.numeroSecuriteSociale = n; }
    public void setProfession(String p) { this.profession = p; }
    public void setPersonneContactNom(String n) { this.personneContactNom = n; }
    public void setPersonneContactTelephone(String t) { this.personneContactTelephone = t; }
    public void setCreatedBy(User u) { this.createdBy = u; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String nom, prenom; private LocalDate dateNaissance;
        private String sexe, telephone, email, adresse, groupeSanguin, allergies, antecedents;
        private String numeroSecuriteSociale, profession, personneContactNom, personneContactTelephone;
        private User createdBy;
        Builder() {}
        public Builder id(Long v) { this.id = v; return this; }
        public Builder nom(String v) { this.nom = v; return this; }
        public Builder prenom(String v) { this.prenom = v; return this; }
        public Builder dateNaissance(LocalDate v) { this.dateNaissance = v; return this; }
        public Builder sexe(String v) { this.sexe = v; return this; }
        public Builder telephone(String v) { this.telephone = v; return this; }
        public Builder email(String v) { this.email = v; return this; }
        public Builder adresse(String v) { this.adresse = v; return this; }
        public Builder groupeSanguin(String v) { this.groupeSanguin = v; return this; }
        public Builder allergies(String v) { this.allergies = v; return this; }
        public Builder antecedents(String v) { this.antecedents = v; return this; }
        public Builder numeroSecuriteSociale(String v) { this.numeroSecuriteSociale = v; return this; }
        public Builder profession(String v) { this.profession = v; return this; }
        public Builder personneContactNom(String v) { this.personneContactNom = v; return this; }
        public Builder personneContactTelephone(String v) { this.personneContactTelephone = v; return this; }
        public Builder createdBy(User v) { this.createdBy = v; return this; }
        public Patient build() {
            Patient p = new Patient(); p.id=id; p.nom=nom; p.prenom=prenom;
            p.dateNaissance=dateNaissance; p.sexe=sexe; p.telephone=telephone;
            p.email=email; p.adresse=adresse; p.groupeSanguin=groupeSanguin;
            p.allergies=allergies; p.antecedents=antecedents;
            p.numeroSecuriteSociale=numeroSecuriteSociale; p.profession=profession;
            p.personneContactNom=personneContactNom;
            p.personneContactTelephone=personneContactTelephone; p.createdBy=createdBy;
            return p;
        }
    }
}
