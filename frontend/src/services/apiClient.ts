import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import toast from 'react-hot-toast';

const API_URL = import.meta.env.VITE_API_URL || '/api/v1';

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

// Intercepteur pour ajouter le token JWT
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('medicare_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ message: string }>) => {
    const status = error.response?.status;
    const message = error.response?.data?.message || error.message;

    if (status === 401) {
      localStorage.removeItem('medicare_token');
      localStorage.removeItem('medicare_refreshToken');
      localStorage.removeItem('medicare_user');
      window.location.href = '/login';
      toast.error('Session expirée. Veuillez vous reconnecter.');
    } else if (status === 403) {
      toast.error('Accès refusé. Vous n\'avez pas les droits nécessaires.');
    } else if (status === 404) {
      toast.error('Ressource non trouvée.');
    } else if (status === 500) {
      toast.error('Erreur serveur. Veuillez réessayer plus tard.');
    }

    return Promise.reject(error);
  }
);

export default apiClient;
