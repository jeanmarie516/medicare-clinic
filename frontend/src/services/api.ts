import apiClient from './apiClient';
import {
  Patient,
  Medecin,
  RendezVous,
  Prescription,
  Facture,
  Message,
  Notification,
  DashboardStats,
  PaginatedResponse,
} from '../types';

// ============================================================
// Patients API
// ============================================================
export const patientApi = {
  getAll: (page = 0, size = 20, search?: string) =>
    apiClient.get<PaginatedResponse<Patient>>('/patients', {
      params: { page, size, search },
    }),

  search: (q: string) =>
    apiClient.get<Patient[]>('/patients/search', { params: { q } }),

  getById: (id: number) =>
    apiClient.get<Patient>(`/patients/${id}`),

  create: (data: Partial<Patient>) =>
    apiClient.post<Patient>('/patients', data),

  update: (id: number, data: Partial<Patient>) =>
    apiClient.put<Patient>(`/patients/${id}`, data),

  delete: (id: number) =>
    apiClient.delete(`/patients/${id}`),
};

// ============================================================
// Médecins API
// ============================================================
export const medecinApi = {
  getAll: () =>
    apiClient.get<Medecin[]>('/medecins'),

  getDisponibles: () =>
    apiClient.get<Medecin[]>('/medecins/disponibles'),

  getById: (id: number) =>
    apiClient.get<Medecin>(`/medecins/${id}`),

  getByUserId: (userId: number) =>
    apiClient.get<Medecin>(`/medecins/user/${userId}`),

  update: (id: number, data: Partial<Medecin>) =>
    apiClient.put<Medecin>(`/medecins/${id}`, data),
};

// ============================================================
// Rendez-Vous API
// ============================================================
export const rendezVousApi = {
  getAll: (page = 0, size = 100, search?: string, medecinId?: number) =>
    apiClient.get<PaginatedResponse<RendezVous>>('/rendez-vous', {
      params: { page, size, search, medecinId },
    }),

  getById: (id: number) =>
    apiClient.get<RendezVous>(`/rendez-vous/${id}`),

  getByMedecinAndDate: (medecinId: number, date: string) =>
    apiClient.get<RendezVous[]>(`/rendez-vous/medecin/${medecinId}/date/${date}`),

  getForCalendar: (debut: string, fin: string, medecinId?: number, statut?: string) =>
    apiClient.get<RendezVous[]>('/rendez-vous/calendrier', {
      params: { debut, fin, medecinId, statut },
    }),

  create: (data: Partial<RendezVous>) =>
    apiClient.post<RendezVous>('/rendez-vous', data),

  update: (id: number, data: Partial<RendezVous>) =>
    apiClient.put<RendezVous>(`/rendez-vous/${id}`, data),

  annuler: (id: number) =>
    apiClient.post<RendezVous>(`/rendez-vous/${id}/annuler`),

  changerStatut: (id: number, statut: string) =>
    apiClient.post<RendezVous>(`/rendez-vous/${id}/statut`, null, {
      params: { statut },
    }),
};

// ============================================================
// Prescriptions API
// ============================================================
export const prescriptionApi = {
  getAll: (page = 0, size = 20) =>
    apiClient.get<PaginatedResponse<Prescription>>('/prescriptions', {
      params: { page, size },
    }),

  getById: (id: number) =>
    apiClient.get<Prescription>(`/prescriptions/${id}`),

  getByPatient: (patientId: number) =>
    apiClient.get<Prescription[]>(`/prescriptions/patient/${patientId}`),

  getPdf: (id: number) =>
    apiClient.get(`/prescriptions/${id}/pdf`, { responseType: 'blob' }),

  create: (data: Partial<Prescription>) =>
    apiClient.post<Prescription>('/prescriptions', data),
};

// ============================================================
// Factures API
// ============================================================
export const factureApi = {
  getAll: (page = 0, size = 20) =>
    apiClient.get<PaginatedResponse<Facture>>('/factures', {
      params: { page, size },
    }),

  getById: (id: number) =>
    apiClient.get<Facture>(`/factures/${id}`),

  getByPatient: (patientId: number) =>
    apiClient.get<Facture[]>(`/factures/patient/${patientId}`),

  getImpayees: () =>
    apiClient.get<Facture[]>('/factures/impayees'),

  create: (data: Partial<Facture>) =>
    apiClient.post<Facture>('/factures', data),

  marquerPayee: (id: number, modePaiement?: string) =>
    apiClient.post<Facture>(`/factures/${id}/payer`, null, {
      params: { modePaiement },
    }),

  paiementPartiel: (id: number, montant: number, modePaiement?: string) =>
    apiClient.post<Facture>(`/factures/${id}/paiement-partiel`, null, {
      params: { montant, modePaiement },
    }),
};

// ============================================================
// Messages API
// ============================================================
export const messageApi = {
  getConversation: (userId: number) =>
    apiClient.get<Message[]>(`/messages/conversation/${userId}`),

  send: (data: Partial<Message>) =>
    apiClient.post<Message>('/messages', data),

  marquerLu: (messageId: number) =>
    apiClient.post(`/messages/${messageId}/lu`),

  marquerConversationLue: (userId: number) =>
    apiClient.post(`/messages/conversation/${userId}/lu`),

  getNonLus: () =>
    apiClient.get<{ count: number }>('/messages/non-lus'),

  getContacts: () =>
    apiClient.get<Array<{ id: number; nom: string; prenom: string; email: string; role: string; photoUrl: string }>>('/messages/contacts'),

  searchUsers: (q: string) =>
    apiClient.get<Array<{ id: number; nom: string; prenom: string; email: string; role: string; photoUrl: string }>>('/messages/search-users', {
      params: { q },
    }),
};

// ============================================================
// Notifications API
// ============================================================
export const notificationApi = {
  getNonLues: () =>
    apiClient.get<Notification[]>('/notifications/non-lues'),

  getAll: (page = 0, size = 20) =>
    apiClient.get<PaginatedResponse<Notification>>('/notifications', {
      params: { page, size },
    }),

  getCount: () =>
    apiClient.get<{ count: number }>('/notifications/count'),

  marquerLu: (id: number) =>
    apiClient.post(`/notifications/${id}/lu`),

  marquerToutLu: () =>
    apiClient.post('/notifications/tout-lu'),
};

// ============================================================
// Dashboard API
// ============================================================
export const dashboardApi = {
  getAdminStats: () =>
    apiClient.get<DashboardStats>('/dashboard/stats'),

  getMedecinStats: () =>
    apiClient.get<DashboardStats>('/dashboard/medecin-stats'),
};

// ============================================================
// Présence API
// ============================================================
export const presenceApi = {
  getOnlineUsers: () =>
    apiClient.get<{ onlineUserIds: number[] }>('/users/online'),
};

// ============================================================
// Configurations API
// ============================================================
export const configurationApi = {
  getAll: () =>
    apiClient.get<Record<string, string>>('/configurations'),

  getByPrefix: (prefix: string) =>
    apiClient.get<Record<string, string>>(`/configurations/prefix/${prefix}`),

  getValue: (cle: string) =>
    apiClient.get<{ cle: string; valeur: string }>(`/configurations/${cle}`),

  save: (configs: Record<string, string>) =>
    apiClient.post('/configurations', configs),

  resetDefaults: () =>
    apiClient.post('/configurations/reset'),
};

// ============================================================
// Admin API
// ============================================================
export const adminApi = {
  getUsers: () =>
    apiClient.get<Array<{ id: number; nom: string; prenom: string; email: string; role: string; actif: boolean; telephone: string; photoUrl: string; createdAt: string }>>('/admin/users'),

  createUser: (data: { nom: string; prenom: string; email: string; password: string; role: string; telephone?: string; specialite?: string }) =>
    apiClient.post('/admin/users', data),

  toggleActif: (id: number) =>
    apiClient.post<{ id: number; actif: boolean; message: string }>(`/admin/users/${id}/toggle-actif`),

  deleteUser: (id: number) =>
    apiClient.delete(`/admin/users/${id}`),

  getAuditLogs: (page = 0, size = 50) =>
    apiClient.get(`/admin/audit-logs`, { params: { page, size } }),
};
