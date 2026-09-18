export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: 'ADMIN' | 'MEDECIN' | 'SECRETAIRE';
  actif: boolean;
  photoUrl?: string;
  telephone?: string;
  createdAt: string;
}

export interface AuthResponse {
  id: number;
  token: string;
  refreshToken: string;
  email: string;
  nom: string;
  prenom: string;
  role: 'ADMIN' | 'MEDECIN' | 'SECRETAIRE';
  photoUrl?: string;
  message: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  password: string;
  role: 'MEDECIN' | 'SECRETAIRE';
  telephone?: string;
  specialite?: string;
}

export interface Patient {
  id: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  sexe: 'M' | 'F';
  telephone?: string;
  email?: string;
  adresse?: string;
  groupeSanguin?: string;
  allergies?: string;
  antecedents?: string;
  numeroSecuriteSociale?: string;
  profession?: string;
  personneContactNom?: string;
  personneContactTelephone?: string;
  createdAt: string;
  createdById: number;
  createdByNom: string;
}

export interface Medecin {
  id: number;
  userId: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  photoUrl?: string;
  specialite: string;
  numeroOrdre?: string;
  disponible: boolean;
  tarifConsultation?: number;
  biographie?: string;
  anneesExperience?: number;
}

export interface RendezVous {
  id: number;
  patientId: number;
  patientNom: string;
  patientPrenom: string;
  patientTelephone?: string;
  medecinId: number;
  medecinNom: string;
  medecinPrenom: string;
  medecinSpecialite: string;
  dateHeure: string;
  dureeMinutes: number;
  statut: StatutRendezVous;
  motif?: string;
  notes?: string;
  salle?: string;
  createdById: number;
  createdAt: string;
}

export type StatutRendezVous =
  | 'EN_ATTENTE'
  | 'CONFIRME'
  | 'EN_COURS'
  | 'TERMINE'
  | 'ANNULE'
  | 'ABSENT';

export type StatutPaiement =
  | 'EN_ATTENTE'
  | 'PARTIELLEMENT_PAYE'
  | 'SOLDE'
  | 'ANNULE';

export interface Prescription {
  id: number;
  patientId: number;
  patientNom: string;
  patientPrenom: string;
  patientTelephone?: string;
  patientEmail?: string;
  patientGroupeSanguin?: string;
  patientAllergies?: string;
  patientAntecedents?: string;
  patientSexe?: string;
  medecinId: number;
  medecinNom: string;
  medecinPrenom: string;
  medecinSpecialite?: string;
  rdvId?: number;
  datePrescription: string;
  medicamentsJson: string;
  instructions?: string;
  valideJusqua?: string;
  diagnostic?: string;
  pdfUrl?: string;
  createdAt: string;
}

export interface MedicamentItem {
  nom: string;
  dosage: string;
  frequence: string;
  duree: string;
  instructions?: string;
}

export interface Facture {
  id: number;
  numeroFacture: string;
  patientId: number;
  patientNom: string;
  patientPrenom: string;
  rdvId?: number;
  montantTotal: number;
  montantPaye: number;
  montantRestant: number;
  tva?: number;
  statutPaiement: StatutPaiement;
  dateFacture: string;
  dateEcheance: string;
  pdfUrl?: string;
  ligneItemsJson?: string;
  modePaiement?: string;
  datePaiement?: string;
  notes?: string;
  createdById: number;
  createdAt: string;
}

export interface LigneFactureItem {
  description: string;
  quantite: number;
  prixUnitaire: number;
  montant: number;
}

export interface Message {
  id: number;
  expediteurId: number;
  expediteurNom: string;
  expediteurPrenom: string;
  destinataireId: number;
  destinataireNom: string;
  destinatairePrenom: string;
  contenu: string;
  lu: boolean;
  delivered: boolean;
  conversationId: string;
  createdAt: string;
  luAt?: string;
  deliveredAt?: string;
}

export interface Notification {
  id: number;
  userId: number;
  type: string;
  titre: string;
  contenu?: string;
  lu: boolean;
  lien?: string;
  createdAt: string;
}

export interface DashboardStats {
  totalPatients: number;
  totalMedecins: number;
  totalSecretaires: number;
  rdvAujourdhui: number;
  rdvEnAttente: number;
  rdvConfirmes: number;
  rdvTermines: number;
  rdvAnnules: number;
  revenusDuMois: number;
  revenusDuJour: number;
  facturesImpayees: number;
  prescriptionsDuMois: number;
  topMedecins: MedecinStat[];
  revenusMensuels: RevenuMensuel[];
  rdvParSpecialite: RdvParSpecialite[];
  activitesRecentes: ActiviteRecente[];
}

export interface MedecinStat {
  medecinId: number;
  nom: string;
  prenom: string;
  specialite: string;
  totalRdv: number;
  rdvTermines: number;
  patientsUniques: number;
}

export interface RevenuMensuel {
  mois: number;
  annee: number;
  montant: number;
  nombreFactures: number;
}

export interface RdvParSpecialite {
  specialite: string;
  nombre: number;
}

export interface ActiviteRecente {
  action: string;
  ressource: string;
  userNom: string;
  detail: string;
  createdAt: string;
}

export interface Configuration {
  [key: string]: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
