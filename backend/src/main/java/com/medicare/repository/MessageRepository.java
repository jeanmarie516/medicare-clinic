package com.medicare.repository;

import com.medicare.entity.Message;
import com.medicare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.expediteur = :user1 AND m.destinataire = :user2) OR " +
           "(m.expediteur = :user2 AND m.destinataire = :user1) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT m FROM Message m WHERE " +
           "(m.expediteur = :user1 AND m.destinataire = :user2) OR " +
           "(m.expediteur = :user2 AND m.destinataire = :user1) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversationPaginated(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.destinataire = :user AND m.lu = false " +
           "ORDER BY m.createdAt DESC")
    List<Message> findMessagesNonLus(@Param("user") User user);

    long countByDestinataireAndLuFalse(User destinataire);

    @Query("SELECT DISTINCT m.destinataire FROM Message m WHERE m.expediteur = :user")
    List<User> findSentToPartners(@Param("user") User user);

    @Query("SELECT DISTINCT m.expediteur FROM Message m WHERE m.destinataire = :user")
    List<User> findReceivedFromPartners(@Param("user") User user);

    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId ORDER BY m.createdAt ASC")
    List<Message> findByConversationId(@Param("conversationId") String conversationId);

    @Query("SELECT m FROM Message m WHERE m.destinataire = :destinataire AND m.delivered = false AND m.expediteur != :destinataire")
    List<Message> findUndeliveredMessages(@Param("destinataire") User destinataire);
}
