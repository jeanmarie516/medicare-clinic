import { create } from 'zustand';
import { User, AuthResponse } from '../types';
import { authApi } from '../services/authApi';

interface AuthState {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  checkAuth: () => Promise<void>;
  clearError: () => void;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  token: localStorage.getItem('medicare_token'),
  refreshToken: localStorage.getItem('medicare_refreshToken'),
  isAuthenticated: !!localStorage.getItem('medicare_token'),
  isLoading: false,
  error: null,

  login: async (email: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      const response = await authApi.login({ email, password });
      const user: User = {
        id: response.id,
        nom: response.nom,
        prenom: response.prenom,
        email: response.email,
        role: response.role,
        actif: true,
        photoUrl: response.photoUrl,
        createdAt: new Date().toISOString(),
      };

      localStorage.setItem('medicare_token', response.token);
      localStorage.setItem('medicare_refreshToken', response.refreshToken);
      localStorage.setItem('medicare_user', JSON.stringify(user));

      set({
        user,
        token: response.token,
        refreshToken: response.refreshToken,
        isAuthenticated: true,
        isLoading: false,
      });
    } catch (error: any) {
      const message = error.response?.data?.message || 'Erreur de connexion';
      set({ isLoading: false, error: message });
      throw new Error(message);
    }
  },

  logout: () => {
    localStorage.removeItem('medicare_token');
    localStorage.removeItem('medicare_refreshToken');
    localStorage.removeItem('medicare_user');
    set({
      user: null,
      token: null,
      refreshToken: null,
      isAuthenticated: false,
    });
  },

  checkAuth: async () => {
    const token = localStorage.getItem('medicare_token');
    
    if (!token) {
      set({ isAuthenticated: false, user: null });
      return;
    }

    try {
      // Vérifier que le token est valide auprès du backend
      const response = await authApi.getCurrentUser();
      const user: User = {
        id: response.id,
        nom: response.nom,
        prenom: response.prenom,
        email: response.email,
        role: response.role,
        actif: true,
        photoUrl: response.photoUrl,
        createdAt: new Date().toISOString(),
      };
      localStorage.setItem('medicare_user', JSON.stringify(user));
      set({ user, token, isAuthenticated: true });
    } catch {
      // Token invalide ou utilisateur supprimé → nettoyage
      localStorage.removeItem('medicare_token');
      localStorage.removeItem('medicare_refreshToken');
      localStorage.removeItem('medicare_user');
      set({ isAuthenticated: false, user: null, token: null });
    }
  },

  clearError: () => set({ error: null }),
}));
