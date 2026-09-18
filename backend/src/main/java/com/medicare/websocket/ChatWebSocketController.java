package com.medicare.websocket;

import com.medicare.dto.MessageDto;
import com.medicare.entity.User;
import com.medicare.repository.UserRepository;
import com.medicare.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {


    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate, MessageService messageService, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDto messageDto, Principal principal) {
        User expediteur = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        messageDto.setExpediteurId(expediteur.getId());
        MessageDto savedMessage = messageService.sendMessage(messageDto, expediteur);

        // Envoyer au destinataire
        messagingTemplate.convertAndSendToUser(
                messageDto.getDestinataireId().toString(),
                "/queue/messages",
                savedMessage
        );

        // Confirmation à l'expéditeur
        messagingTemplate.convertAndSendToUser(
                expediteur.getId().toString(),
                "/queue/messages",
                savedMessage
        );
    }

    @MessageMapping("/chat.markRead")
    public void markAsRead(@Payload Long messageId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        messageService.marquerCommeLu(messageId, user);
    }
}
