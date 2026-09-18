package com.medicare.config;

import com.medicare.entity.Message;
import com.medicare.entity.User;
import com.medicare.repository.MessageRepository;
import com.medicare.service.UserPresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final UserPresenceService presenceService;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(UserPresenceService presenceService,
                                   MessageRepository messageRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.presenceService = presenceService;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() instanceof Authentication auth) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User user) {
                presenceService.userConnected(user.getId());
                log.debug("[WS Présence] {} connecté - {} en ligne", user.getEmail(), presenceService.getOnlineCount());

                // Marquer les messages non délivrés comme délivrés ✓✓
                marquerMessagesDelivered(user);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() instanceof Authentication auth) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User user) {
                presenceService.userDisconnected(user.getId());
                log.debug("[WS Présence] {} déconnecté - {} en ligne", user.getEmail(), presenceService.getOnlineCount());
            }
        }
    }

    /**
     * Quand un utilisateur se connecte, tous les messages qui lui étaient destinés
     * et qui n'étaient pas encore délivrés passent à l'état ✓✓ (délivré).
     * Utilise un Map pour éviter de sérialiser les entités JPA (avec leurs relations lazy).
     */
    @Transactional
    protected void marquerMessagesDelivered(User destinataire) {
        try {
            List<Message> undelivered = messageRepository.findUndeliveredMessages(destinataire);
            if (undelivered.isEmpty()) return;

            log.debug("[WS Délivré] Marquage de {} messages comme délivrés pour {}", undelivered.size(), destinataire.getEmail());

            LocalDateTime now = LocalDateTime.now();
            for (Message msg : undelivered) {
                msg.setDelivered(true);
                msg.setDeliveredAt(now);
                messageRepository.save(msg);

                // Notifier l'expéditeur avec un Map (évite les problèmes de sérialisation des entités JPA)
                Map<String, Object> statusUpdate = new HashMap<>();
                statusUpdate.put("id", msg.getId());
                statusUpdate.put("delivered", true);
                statusUpdate.put("deliveredAt", now.toString());
                statusUpdate.put("lu", msg.getLu());
                statusUpdate.put("conversationId", msg.getConversationId());

                try {
                    messagingTemplate.convertAndSendToUser(
                            msg.getExpediteur().getId().toString(),
                            "/queue/messages",
                            statusUpdate
                    );
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            log.error("[WS Délivré] Erreur lors du marquage des messages délivrés", e);
        }
    }
}
