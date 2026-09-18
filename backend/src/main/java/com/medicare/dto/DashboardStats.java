package com.medicare.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStats {
    private long totalPatients, totalMedecins, totalSecretaires;
    private long rdvAujourdhui, rdvEnAttente, rdvConfirmes, rdvTermines, rdvAnnules;
    private BigDecimal revenusDuMois, revenusDuJour;
    private long facturesImpayees, prescriptionsDuMois;
    private List<MedecinStat> topMedecins;
    private List<RevenuMensuel> revenusMensuels;
    private List<RdvParSpecialite> rdvParSpecialite;
    private List<ActiviteRecente> activitesRecentes;

    public DashboardStats() {}

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long v) { this.totalPatients=v; }
    public long getTotalMedecins() { return totalMedecins; }
    public void setTotalMedecins(long v) { this.totalMedecins=v; }
    public long getTotalSecretaires() { return totalSecretaires; }
    public void setTotalSecretaires(long v) { this.totalSecretaires=v; }
    public long getRdvAujourdhui() { return rdvAujourdhui; }
    public void setRdvAujourdhui(long v) { this.rdvAujourdhui=v; }
    public long getRdvEnAttente() { return rdvEnAttente; }
    public void setRdvEnAttente(long v) { this.rdvEnAttente=v; }
    public long getRdvConfirmes() { return rdvConfirmes; }
    public void setRdvConfirmes(long v) { this.rdvConfirmes=v; }
    public long getRdvTermines() { return rdvTermines; }
    public void setRdvTermines(long v) { this.rdvTermines=v; }
    public long getRdvAnnules() { return rdvAnnules; }
    public void setRdvAnnules(long v) { this.rdvAnnules=v; }
    public BigDecimal getRevenusDuMois() { return revenusDuMois; }
    public void setRevenusDuMois(BigDecimal v) { this.revenusDuMois=v; }
    public BigDecimal getRevenusDuJour() { return revenusDuJour; }
    public void setRevenusDuJour(BigDecimal v) { this.revenusDuJour=v; }
    public long getFacturesImpayees() { return facturesImpayees; }
    public void setFacturesImpayees(long v) { this.facturesImpayees=v; }
    public long getPrescriptionsDuMois() { return prescriptionsDuMois; }
    public void setPrescriptionsDuMois(long v) { this.prescriptionsDuMois=v; }
    public List<MedecinStat> getTopMedecins() { return topMedecins; }
    public void setTopMedecins(List<MedecinStat> v) { this.topMedecins=v; }
    public List<RevenuMensuel> getRevenusMensuels() { return revenusMensuels; }
    public void setRevenusMensuels(List<RevenuMensuel> v) { this.revenusMensuels=v; }
    public List<RdvParSpecialite> getRdvParSpecialite() { return rdvParSpecialite; }
    public void setRdvParSpecialite(List<RdvParSpecialite> v) { this.rdvParSpecialite=v; }
    public List<ActiviteRecente> getActivitesRecentes() { return activitesRecentes; }
    public void setActivitesRecentes(List<ActiviteRecente> v) { this.activitesRecentes=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private long totalPatients, totalMedecins, totalSecretaires;
        private long rdvAujourdhui, rdvEnAttente, rdvConfirmes, rdvTermines, rdvAnnules;
        private long facturesImpayees, prescriptionsDuMois;
        private BigDecimal revenusDuMois, revenusDuJour;
        private List<MedecinStat> topMedecins;
        private List<RevenuMensuel> revenusMensuels;
        private List<RdvParSpecialite> rdvParSpecialite;
        private List<ActiviteRecente> activitesRecentes;
        Builder() {}
        public Builder totalPatients(long v) { this.totalPatients=v; return this; }
        public Builder totalMedecins(long v) { this.totalMedecins=v; return this; }
        public Builder totalSecretaires(long v) { this.totalSecretaires=v; return this; }
        public Builder rdvAujourdhui(long v) { this.rdvAujourdhui=v; return this; }
        public Builder rdvEnAttente(long v) { this.rdvEnAttente=v; return this; }
        public Builder rdvConfirmes(long v) { this.rdvConfirmes=v; return this; }
        public Builder rdvTermines(long v) { this.rdvTermines=v; return this; }
        public Builder rdvAnnules(long v) { this.rdvAnnules=v; return this; }
        public Builder revenusDuMois(BigDecimal v) { this.revenusDuMois=v; return this; }
        public Builder revenusDuJour(BigDecimal v) { this.revenusDuJour=v; return this; }
        public Builder facturesImpayees(long v) { this.facturesImpayees=v; return this; }
        public Builder prescriptionsDuMois(long v) { this.prescriptionsDuMois=v; return this; }
        public Builder topMedecins(List<MedecinStat> v) { this.topMedecins=v; return this; }
        public Builder revenusMensuels(List<RevenuMensuel> v) { this.revenusMensuels=v; return this; }
        public Builder rdvParSpecialite(List<RdvParSpecialite> v) { this.rdvParSpecialite=v; return this; }
        public Builder activitesRecentes(List<ActiviteRecente> v) { this.activitesRecentes=v; return this; }
        public DashboardStats build() {
            DashboardStats d = new DashboardStats(); d.totalPatients=totalPatients;
            d.totalMedecins=totalMedecins; d.totalSecretaires=totalSecretaires;
            d.rdvAujourdhui=rdvAujourdhui; d.rdvEnAttente=rdvEnAttente;
            d.rdvConfirmes=rdvConfirmes; d.rdvTermines=rdvTermines; d.rdvAnnules=rdvAnnules;
            d.revenusDuMois=revenusDuMois; d.revenusDuJour=revenusDuJour;
            d.facturesImpayees=facturesImpayees; d.prescriptionsDuMois=prescriptionsDuMois;
            d.topMedecins=topMedecins; d.revenusMensuels=revenusMensuels;
            d.rdvParSpecialite=rdvParSpecialite; d.activitesRecentes=activitesRecentes; return d;
        }
    }

    public static class MedecinStat {
        private Long medecinId; private String nom, prenom, specialite;
        private long totalRdv, rdvTermines, patientsUniques;
        public MedecinStat() {}
        public Long getMedecinId() { return medecinId; }
        public void setMedecinId(Long v) { this.medecinId=v; }
        public String getNom() { return nom; }
        public void setNom(String v) { this.nom=v; }
        public String getPrenom() { return prenom; }
        public void setPrenom(String v) { this.prenom=v; }
        public String getSpecialite() { return specialite; }
        public void setSpecialite(String v) { this.specialite=v; }
        public long getTotalRdv() { return totalRdv; }
        public void setTotalRdv(long v) { this.totalRdv=v; }
        public long getRdvTermines() { return rdvTermines; }
        public void setRdvTermines(long v) { this.rdvTermines=v; }
        public long getPatientsUniques() { return patientsUniques; }
        public void setPatientsUniques(long v) { this.patientsUniques=v; }
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long medecinId; private String nom, prenom, specialite;
            private long totalRdv, rdvTermines, patientsUniques;
            Builder() {}
            public Builder medecinId(Long v) { this.medecinId=v; return this; }
            public Builder nom(String v) { this.nom=v; return this; }
            public Builder prenom(String v) { this.prenom=v; return this; }
            public Builder specialite(String v) { this.specialite=v; return this; }
            public Builder totalRdv(long v) { this.totalRdv=v; return this; }
            public Builder rdvTermines(long v) { this.rdvTermines=v; return this; }
            public Builder patientsUniques(long v) { this.patientsUniques=v; return this; }
            public MedecinStat build() {
                MedecinStat s = new MedecinStat(); s.medecinId=medecinId; s.nom=nom;
                s.prenom=prenom; s.specialite=specialite; s.totalRdv=totalRdv;
                s.rdvTermines=rdvTermines; s.patientsUniques=patientsUniques; return s;
            }
        }
    }

    public static class RevenuMensuel {
        private int mois, annee; private BigDecimal montant; private long nombreFactures;
        public RevenuMensuel() {}
        public int getMois() { return mois; }
        public void setMois(int v) { this.mois=v; }
        public int getAnnee() { return annee; }
        public void setAnnee(int v) { this.annee=v; }
        public BigDecimal getMontant() { return montant; }
        public void setMontant(BigDecimal v) { this.montant=v; }
        public long getNombreFactures() { return nombreFactures; }
        public void setNombreFactures(long v) { this.nombreFactures=v; }
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private int mois, annee; private BigDecimal montant; private long nombreFactures;
            Builder() {}
            public Builder mois(int v) { this.mois=v; return this; }
            public Builder annee(int v) { this.annee=v; return this; }
            public Builder montant(BigDecimal v) { this.montant=v; return this; }
            public Builder nombreFactures(long v) { this.nombreFactures=v; return this; }
            public RevenuMensuel build() {
                RevenuMensuel r = new RevenuMensuel(); r.mois=mois; r.annee=annee;
                r.montant=montant; r.nombreFactures=nombreFactures; return r;
            }
        }
    }

    public static class RdvParSpecialite {
        private String specialite; private long nombre;
        public RdvParSpecialite() {}
        public String getSpecialite() { return specialite; }
        public void setSpecialite(String v) { this.specialite=v; }
        public long getNombre() { return nombre; }
        public void setNombre(long v) { this.nombre=v; }
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String specialite; private long nombre;
            Builder() {}
            public Builder specialite(String v) { this.specialite=v; return this; }
            public Builder nombre(long v) { this.nombre=v; return this; }
            public RdvParSpecialite build() {
                RdvParSpecialite r = new RdvParSpecialite(); r.specialite=specialite; r.nombre=nombre; return r;
            }
        }
    }

    public static class ActiviteRecente {
        private String action, ressource, userNom, detail, createdAt;
        public ActiviteRecente() {}
        public String getAction() { return action; }
        public void setAction(String v) { this.action=v; }
        public String getRessource() { return ressource; }
        public void setRessource(String v) { this.ressource=v; }
        public String getUserNom() { return userNom; }
        public void setUserNom(String v) { this.userNom=v; }
        public String getDetail() { return detail; }
        public void setDetail(String v) { this.detail=v; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String v) { this.createdAt=v; }
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String action, ressource, userNom, detail, createdAt;
            Builder() {}
            public Builder action(String v) { this.action=v; return this; }
            public Builder ressource(String v) { this.ressource=v; return this; }
            public Builder userNom(String v) { this.userNom=v; return this; }
            public Builder detail(String v) { this.detail=v; return this; }
            public Builder createdAt(String v) { this.createdAt=v; return this; }
            public ActiviteRecente build() {
                ActiviteRecente a = new ActiviteRecente(); a.action=action; a.ressource=ressource;
                a.userNom=userNom; a.detail=detail; a.createdAt=createdAt; return a;
            }
        }
    }
}
