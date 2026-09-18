import { create } from 'zustand';
import { Message } from '../types';
import { messageApi, presenceApi } from '../services/api';

interface ChatState {
  conversations: { [userId: number]: Message[] };
  contacts: Array<{ id: number; nom: string; prenom: string; email: string; role: string; photoUrl: string }>;
  selectedContactId: number | null;
  messagesNonLus: number;
  isLoading: boolean;
  currentUserId: number | null;
  setCurrentUser: (id: number) => void;
  fetchContacts: () => Promise<void>;
  selectContact: (userId: number) => Promise<void>;
  sendMessage: (message: Partial<Message>) => Promise<void>;
  refreshConversation: (contactId: number) => Promise<void>;
  startPolling: () => void;
  stopPolling: () => void;
  fetchMessagesNonLus: () => Promise<void>;
  onlineUserIds: number[];
  fetchOnlineUsers: () => Promise<void>;
}

let pollingInterval: ReturnType<typeof setInterval> | null = null;

export const useChatStore = create<ChatState>((set, get) => ({
  conversations: {},
  contacts: [],
  selectedContactId: null,
  messagesNonLus: 0,
  isLoading: false,
  currentUserId: null,
  onlineUserIds: [],

  setCurrentUser: (id: number) => set({ currentUserId: id }),

  fetchContacts: async () => {
    try {
      const res = await messageApi.getContacts();
      set({ contacts: res.data });
    } catch (error) {
      console.error('Erreur chargement contacts:', error);
    }
  },

  selectContact: async (userId: number) => {
    set({ isLoading: true, selectedContactId: userId });
    await get().refreshConversation(userId);

    // Marquer conversation comme lue
    try {
      await messageApi.marquerConversationLue(userId);
      await get().fetchMessagesNonLus();
    } catch (error) {
      console.error('Erreur marquage lu:', error);
    }
  },

  refreshConversation: async (contactId: number) => {
    try {
      const res = await messageApi.getConversation(contactId);
      set((state) => ({
        conversations: { ...state.conversations, [contactId]: res.data },
        isLoading: false,
      }));
    } catch (error) {
      set({ isLoading: false });
      console.error('Erreur chargement conversation:', error);
    }
  },

  sendMessage: async (message: Partial<Message>) => {
    try {
      const { currentUserId, selectedContactId } = get();
      if (!currentUserId) {
        console.error('currentUserId non défini');
        return;
      }
      // Inclure l'ID de l'expéditeur (obligatoire pour la validation backend)
      const messageToSend = { ...message, expediteurId: currentUserId };
      const res = await messageApi.send(messageToSend);
      const contactId = selectedContactId || res.data.destinataireId;

      if (contactId) {
        // Ajouter le message immédiatement
        set((state) => {
          const existing = state.conversations[contactId] || [];
          const exists = existing.some(m => m.id === res.data.id);
          if (exists) return state;
          return {
            conversations: { ...state.conversations, [contactId]: [...existing, res.data] },
          };
        });
      }
    } catch (error) {
      console.error('Erreur envoi message:', error);
    }
  },

  startPolling: () => {
    if (pollingInterval) return;
    let pollCount = 0;
    // Premier appel immédiat pour les statuts en ligne
    get().fetchOnlineUsers();
    pollingInterval = setInterval(() => {
      pollCount++;
      const { selectedContactId } = get();
      // Rafraîchir les statuts en ligne toutes les 4s
      if (pollCount % 2 === 0) {
        get().fetchOnlineUsers();
      }
      // Rafraîchir les contacts moins souvent (toutes les 10s)
      if (pollCount % 5 === 0) {
        get().fetchContacts();
      }
      get().fetchMessagesNonLus();
      if (selectedContactId) {
        get().refreshConversation(selectedContactId);
      }
    }, 2000);
  },

  stopPolling: () => {
    if (pollingInterval) {
      clearInterval(pollingInterval);
      pollingInterval = null;
    }
  },

  fetchMessagesNonLus: async () => {
    try {
      const res = await messageApi.getNonLus();
      set({ messagesNonLus: res.data.count });
    } catch (error) {
      console.error('Erreur chargement messages non lus:', error);
    }
  },

  fetchOnlineUsers: async () => {
    try {
      const res = await presenceApi.getOnlineUsers();
      set({ onlineUserIds: res.data.onlineUserIds });
    } catch (error) {
      // Silencieux — pas critique
    }
  },
}));
