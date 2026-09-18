import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';
const WS_URL = API_URL.replace('/api/v1', '/ws');

let stompClient: Client | null = null;
let isConnected = false;

type MessageHandler = (payload: any) => void;

const handlers: Record<string, MessageHandler[]> = {
  message: [],
  notification: [],
};

export const websocketService = {
  connect: (token: string) => {
    if (isConnected) return;

    stompClient = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        isConnected = true;
        console.log('[WS] Connecté au WebSocket');

        // Souscrire aux messages en temps réel
        stompClient?.subscribe('/user/queue/messages', (message: IMessage) => {
          try {
            const payload = JSON.parse(message.body);
            handlers.message.forEach((handler) => handler(payload));
          } catch (e) {
            console.error('[WS] Erreur parsing message:', e);
          }
        });

        // Souscrire aux notifications en temps réel
        stompClient?.subscribe('/user/queue/notifications', (message: IMessage) => {
          try {
            const payload = JSON.parse(message.body);
            handlers.notification.forEach((handler) => handler(payload));
          } catch (e) {
            console.error('[WS] Erreur parsing notification:', e);
          }
        });
      },
      onDisconnect: () => {
        isConnected = false;
        console.log('[WS] Déconnecté du WebSocket');
      },
      onStompError: (frame) => {
        console.error('[WS] Erreur STOMP:', frame.headers['message']);
        isConnected = false;
      },
    });

    stompClient.activate();
  },

  disconnect: () => {
    if (stompClient) {
      stompClient.deactivate();
      stompClient = null;
      isConnected = false;
    }
  },

  onMessage: (handler: MessageHandler) => {
    handlers.message.push(handler);
    return () => {
      const idx = handlers.message.indexOf(handler);
      if (idx >= 0) handlers.message.splice(idx, 1);
    };
  },

  onNotification: (handler: MessageHandler) => {
    handlers.notification.push(handler);
    return () => {
      const idx = handlers.notification.indexOf(handler);
      if (idx >= 0) handlers.notification.splice(idx, 1);
    };
  },

  isConnected: () => isConnected,
};
