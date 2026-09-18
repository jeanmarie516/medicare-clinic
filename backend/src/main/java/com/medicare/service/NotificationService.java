package com.medicare.service;

import com.medicare.dto.NotificationDto;
import com.medicare.entity.Notification;
import com.medicare.entity.User;
import com.medicare.repository.NotificationRepository;
import com.medicare.websocket.NotificationWebSocketController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {


    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketController webSocketController;

    public NotificationService(NotificationRepository notificationRepository, NotificationWebSocketController webSocketController) {
        this.notificationRepository = notificationRepository;
        this.webSocketController = webSocketController;
    }

    public void createNotification(User user, String type, String titre, String contenu, String lien) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .titre(titre)
                .contenu(contenu)
                .lu(false)
                .lien(lien)
                .build();

        notification = notificationRepository.save(notification);

        try {
            webSocketController.sendNotification(
                    user.getId(),
                    toDto(notification)
            );
        } catch (Exception e) {
            // WebSocket non disponible
        }
    }

    public List<NotificationDto> getNotificationsNonLues(User user) {
        return notificationRepository.findByUserAndLuFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<NotificationDto> getAllNotifications(User user, int page, int size) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size))
                .map(this::toDto);
    }

    public long getNombreNotificationsNonLues(User user) {
        return notificationRepository.countByUserAndLuFalse(user);
    }

    @Transactional
    public void marquerCommeLu(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        if (notification.getUser().getId().equals(user.getId())) {
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void marquerToutCommeLu(User user) {
        notificationRepository.marquerToutCommeLu(user);
    }

    public NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .titre(notification.getTitre())
                .contenu(notification.getContenu())
                .lu(notification.getLu())
                .lien(notification.getLien())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
