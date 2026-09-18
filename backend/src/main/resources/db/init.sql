-- ============================================================
-- MediCare Clinic Management System - Initialisation BDD
-- ============================================================
-- Note : Les données initiales sont gérées par DatabaseSeeder.java
-- L'unique administrateur est créé automatiquement au démarrage.

-- Création des tables (si elles n'existent pas déjà via JPA)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT true,
    photo_url VARCHAR(255),
    telephone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    date_naissance DATE NOT NULL,
    sexe VARCHAR(10) NOT NULL,
    telephone VARCHAR(20),
    email VARCHAR(100),
    adresse TEXT,
    groupe_sanguin VARCHAR(5),
    allergies TEXT,
    antecedents TEXT,
    numero_securite_sociale VARCHAR(50),
    profession VARCHAR(100),
    personne_contact_nom VARCHAR(100),
    personne_contact_telephone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS medecins (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    specialite VARCHAR(100) NOT NULL,
    numero_ordre VARCHAR(50) UNIQUE,
    disponible BOOLEAN NOT NULL DEFAULT true,
    tarif_consultation DECIMAL(10,2),
    biographie TEXT,
    annees_experience INTEGER
);

CREATE TABLE IF NOT EXISTS rendez_vous (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    medecin_id BIGINT NOT NULL REFERENCES medecins(id),
    date_heure TIMESTAMP NOT NULL,
    duree_minutes INTEGER NOT NULL DEFAULT 30,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    motif TEXT,
    notes TEXT,
    salle VARCHAR(50),
    created_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    medecin_id BIGINT NOT NULL REFERENCES medecins(id),
    rdv_id BIGINT REFERENCES rendez_vous(id),
    date_prescription DATE NOT NULL DEFAULT CURRENT_DATE,
    medicaments_json TEXT NOT NULL,
    instructions TEXT,
    valide_jusqua DATE,
    diagnostic TEXT,
    pdf_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS factures (
    id BIGSERIAL PRIMARY KEY,
    numero_facture VARCHAR(50) UNIQUE,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    rdv_id BIGINT REFERENCES rendez_vous(id),
    montant_total DECIMAL(10,2) NOT NULL,
    montant_paye DECIMAL(10,2) NOT NULL DEFAULT 0,
    tva DECIMAL(10,2),
    statut_paiement VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    date_facture DATE NOT NULL DEFAULT CURRENT_DATE,
    date_echeance DATE,
    pdf_url VARCHAR(255),
    ligne_items_json TEXT,
    mode_paiement VARCHAR(30),
    date_paiement DATE,
    notes TEXT,
    created_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    expediteur_id BIGINT NOT NULL REFERENCES users(id),
    destinataire_id BIGINT NOT NULL REFERENCES users(id),
    contenu TEXT NOT NULL,
    lu BOOLEAN NOT NULL DEFAULT false,
    delivered BOOLEAN NOT NULL DEFAULT false,
    conversation_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lu_at TIMESTAMP,
    delivered_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(50),
    titre VARCHAR(200) NOT NULL,
    contenu TEXT,
    lu BOOLEAN NOT NULL DEFAULT false,
    lien VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    ressource VARCHAR(100) NOT NULL,
    detail TEXT,
    ip VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS configurations (
    id BIGSERIAL PRIMARY KEY,
    cle VARCHAR(100) NOT NULL UNIQUE,
    valeur TEXT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_configurations_cle ON configurations(cle);
CREATE INDEX IF NOT EXISTS idx_patients_nom ON patients(nom);
CREATE INDEX IF NOT EXISTS idx_rendez_vous_date ON rendez_vous(date_heure);
CREATE INDEX IF NOT EXISTS idx_rendez_vous_medecin ON rendez_vous(medecin_id);
CREATE INDEX IF NOT EXISTS idx_rendez_vous_patient ON rendez_vous(patient_id);
CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created ON audit_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_factures_statut ON factures(statut_paiement);
CREATE INDEX IF NOT EXISTS idx_prescriptions_patient ON prescriptions(patient_id);
