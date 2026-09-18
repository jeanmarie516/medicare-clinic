package com.medicare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "medecins")
public class Medecin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false, unique = true) private User user;
    @Column(nullable = false, length = 100) private String specialite;
    @Column(name = "numero_ordre", length = 50, unique = true) private String numeroOrdre;
    @Column(nullable = false) private Boolean disponible = true;
    @Column(name = "tarif_consultation", precision = 10, scale = 2) private BigDecimal tarifConsultation;
    @Column(columnDefinition = "TEXT") private String biographie;
    @Column(name = "annees_experience") private Integer anneesExperience;

    public Medecin() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getSpecialite() { return specialite; }
    public String getNumeroOrdre() { return numeroOrdre; }
    public Boolean getDisponible() { return disponible; }
    public BigDecimal getTarifConsultation() { return tarifConsultation; }
    public String getBiographie() { return biographie; }
    public Integer getAnneesExperience() { return anneesExperience; }

    public void setId(Long v) { this.id = v; }
    public void setUser(User v) { this.user = v; }
    public void setSpecialite(String v) { this.specialite = v; }
    public void setNumeroOrdre(String v) { this.numeroOrdre = v; }
    public void setDisponible(Boolean v) { this.disponible = v; }
    public void setTarifConsultation(BigDecimal v) { this.tarifConsultation = v; }
    public void setBiographie(String v) { this.biographie = v; }
    public void setAnneesExperience(Integer v) { this.anneesExperience = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private User user; private String specialite, numeroOrdre;
        private Boolean disponible = true; private BigDecimal tarifConsultation;
        private String biographie; private Integer anneesExperience;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder user(User v) { this.user=v; return this; }
        public Builder specialite(String v) { this.specialite=v; return this; }
        public Builder numeroOrdre(String v) { this.numeroOrdre=v; return this; }
        public Builder disponible(Boolean v) { this.disponible=v; return this; }
        public Builder tarifConsultation(BigDecimal v) { this.tarifConsultation=v; return this; }
        public Builder biographie(String v) { this.biographie=v; return this; }
        public Builder anneesExperience(Integer v) { this.anneesExperience=v; return this; }
        public Medecin build() {
            Medecin m = new Medecin(); m.id=id; m.user=user; m.specialite=specialite;
            m.numeroOrdre=numeroOrdre; m.disponible=disponible;
            m.tarifConsultation=tarifConsultation; m.biographie=biographie;
            m.anneesExperience=anneesExperience; return m;
        }
    }
}
