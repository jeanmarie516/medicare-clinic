package com.medicare.repository;

import com.medicare.entity.Notification;
import com.medicare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndLuFalseOrderByCreatedAtDesc(User user);

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUserAndLuFalse(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.user = :user AND n.lu = false")
    void marquerToutCommeLu(@Param("user") User user);

    void deleteByUser(User user);

    boolean existsByTypeAndLien(String type, String lien);
}
