package com.medicare.service;

import com.medicare.dto.MessageDto;
import com.medicare.entity.Message;
import com.medicare.entity.User;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.MessageRepository;
import com.medicare.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserPresenceService presenceService;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          SimpMessagingTemplate messagingTemplate,
                          UserPresenceService presenceService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.presenceService = presenceService;
    }

    public List<MessageDto> getConversation(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return messageRepository.findConversation(user1, user2).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto sendMessage(MessageDto dto, User expediteur) {
        User destinataire = userRepository.findById(dto.getDestinataireId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire non trouvé"));

        String conversationId = dto.getConversationId();
        if (conversationId == null) {
            conversationId = UUID.randomUUID().toString();
        }

        // ÉTAPE 1 : Créer le message avec delivered=false (état ✓ = envoyé non délivré)
        Message message = Message.builder()
                .expediteur(expediteur)
                .destinataire(destinataire)
                .contenu(dto.getContenu())
                .lu(false)
                .delivered(false)
                .conversationId(conversationId)
                .build();

        message = messageRepository.save(message);

        // ÉTAPE 2 : Vérifier si le destinataire est en ligne via UserPresenceService
        boolean recipientOnline = presenceService.isOnline(destinataire.getId());

        // ÉTAPE 3 : Si en ligne, marquer comme délivré ✓✓ immédiatement
        if (recipientOnline) {
            message.setDelivered(true);
            message.setDeliveredAt(LocalDateTime.now());
            message = messageRepository.save(message);
        }

        MessageDto savedDto = toDto(message);

        // ÉTAPE 4 : Publier en temps réel via WebSocket
        try {
            messagingTemplate.convertAndSendToUser(
                    destinataire.getId().toString(),
                    "/queue/messages",
                    savedDto
            );
        } catch (Exception ignored) {}

        try {
            messagingTemplate.convertAndSendToUser(
                    expediteur.getId().toString(),
                    "/queue/messages",
                    savedDto
            );
        } catch (Exception ignored) {}

        return savedDto;
    }

    @Transactional
    public void marquerCommeLu(Long messageId, User user) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message non trouvé"));
        if (message.getDestinataire().getId().equals(user.getId())) {
            message.setLu(true);
            message.setLuAt(LocalDateTime.now());
            message = messageRepository.save(message);

            // Notifier l'expéditeur que le message a été lu ✓✓ bleu
            try {
                MessageDto updatedDto = toDto(message);
                messagingTemplate.convertAndSendToUser(
                        message.getExpediteur().getId().toString(),
                        "/queue/messages",
                        updatedDto
                );
            } catch (Exception e) {
                // Silencieux
            }
        }
    }

    @Transactional
    public void marquerConversationCommeLu(Long expediteurId, User destinataire) {
        User expediteur = userRepository.findById(expediteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Expéditeur non trouvé"));
        List<Message> messages = messageRepository.findConversation(expediteur, destinataire);
        messages.stream()
                .filter(m -> m.getDestinataire().getId().equals(destinataire.getId()) && !m.getLu())
                .forEach(m -> {
                    m.setLu(true);
                    m.setLuAt(LocalDateTime.now());
                    Message saved = messageRepository.save(m);
                    // Notifier l'expéditeur que son message a été lu ✓✓ bleu
                    try {
                        MessageDto updatedDto = toDto(saved);
                        messagingTemplate.convertAndSendToUser(
                                saved.getExpediteur().getId().toString(),
                                "/queue/messages",
                                updatedDto
                        );
                    } catch (Exception e) {
                        // Silencieux
                    }
                });
    }

    public long getNombreMessagesNonLus(User user) {
        return messageRepository.countByDestinataireAndLuFalse(user);
    }

    public List<User> getConversationPartners(User user) {
        java.util.Set<User> partners = new java.util.HashSet<>();
        partners.addAll(messageRepository.findSentToPartners(user));
        partners.addAll(messageRepository.findReceivedFromPartners(user));
        return new java.util.ArrayList<>(partners);
    }

    public MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .expediteurId(message.getExpediteur().getId())
                .expediteurNom(message.getExpediteur().getNom())
                .expediteurPrenom(message.getExpediteur().getPrenom())
                .destinataireId(message.getDestinataire().getId())
                .destinataireNom(message.getDestinataire().getNom())
                .destinatairePrenom(message.getDestinataire().getPrenom())
                .contenu(message.getContenu())
                .lu(message.getLu())
                .delivered(message.getDelivered())
                .conversationId(message.getConversationId())
                .createdAt(message.getCreatedAt())
                .luAt(message.getLuAt())
                .deliveredAt(message.getDeliveredAt())
                .build();
    }
}
