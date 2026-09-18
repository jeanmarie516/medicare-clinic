import { create } from 'zustand';
import { Notification } from '../types';
import { notificationApi } from '../services/api';

interface NotificationState {
  notifications: Notification[];
  nonLuCount: number;
  isLoading: boolean;
  fetchNotifications: () => Promise<void>;
  marquerLu: (id: number) => Promise<void>;
  marquerToutLu: () => Promise<void>;
  addNotification: (notification: Notification) => void;
}

export const useNotificationStore = create<NotificationState>((set, get) => ({
  notifications: [],
  nonLuCount: 0,
  isLoading: false,

  fetchNotifications: async () => {
    try {
      const [nonLuesRes, countRes] = await Promise.all([
        notificationApi.getNonLues(),
        notificationApi.getCount(),
      ]);
      set({
        notifications: nonLuesRes.data,
        nonLuCount: countRes.data.count,
      });
    } catch (error) {
      console.error('Erreur chargement notifications:', error);
    }
  },

  marquerLu: async (id: number) => {
    try {
      await notificationApi.marquerLu(id);
      const { notifications, nonLuCount } = get();
      set({
        notifications: notifications.map((n) =>
          n.id === id ? { ...n, lu: true } : n
        ),
        nonLuCount: Math.max(0, nonLuCount - 1),
      });
    } catch (error) {
      console.error('Erreur marquage notification:', error);
    }
  },

  marquerToutLu: async () => {
    try {
      await notificationApi.marquerToutLu();
      set({
        notifications: get().notifications.map((n) => ({ ...n, lu: true })),
        nonLuCount: 0,
      });
    } catch (error) {
      console.error('Erreur marquage tout lu:', error);
    }
  },

  addNotification: (notification: Notification) => {
    set((state) => ({
      notifications: [notification, ...state.notifications],
      nonLuCount: state.nonLuCount + 1,
    }));
  },
}));
