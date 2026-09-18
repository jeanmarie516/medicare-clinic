package com.medicare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "expediteur_id", nullable = false) private User expediteur;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "destinataire_id", nullable = false) private User destinataire;
    @Column(columnDefinition = "TEXT", nullable = false) private String contenu;
    @Column(nullable = false) private Boolean lu = false;
    @Column private Boolean delivered = false;
    @Column(name = "conversation_id", length = 100) private String conversationId;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "lu_at") private LocalDateTime luAt;
    @Column(name = "delivered_at") private LocalDateTime deliveredAt;

    public Message() {}

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public User getExpediteur() { return expediteur; }
    public User getDestinataire() { return destinataire; }
    public String getContenu() { return contenu; }
    public Boolean getLu() { return lu; }
    public Boolean getDelivered() { return delivered; }
    public String getConversationId() { return conversationId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLuAt() { return luAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    public void setId(Long v) { this.id = v; }
    public void setExpediteur(User v) { this.expediteur = v; }
    public void setDestinataire(User v) { this.destinataire = v; }
    public void setContenu(String v) { this.contenu = v; }
    public void setLu(Boolean v) { this.lu = v; }
    public void setDelivered(Boolean v) { this.delivered = v; }
    public void setConversationId(String v) { this.conversationId = v; }
    public void setLuAt(LocalDateTime v) { this.luAt = v; }
    public void setDeliveredAt(LocalDateTime v) { this.deliveredAt = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private User expediteur, destinataire;
        private String contenu; private Boolean lu = false;
        private Boolean delivered = false;
        private String conversationId;
        private LocalDateTime luAt, deliveredAt;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder expediteur(User v) { this.expediteur=v; return this; }
        public Builder destinataire(User v) { this.destinataire=v; return this; }
        public Builder contenu(String v) { this.contenu=v; return this; }
        public Builder lu(Boolean v) { this.lu=v; return this; }
        public Builder delivered(Boolean v) { this.delivered=v; return this; }
        public Builder conversationId(String v) { this.conversationId=v; return this; }
        public Builder luAt(LocalDateTime v) { this.luAt=v; return this; }
        public Builder deliveredAt(LocalDateTime v) { this.deliveredAt=v; return this; }
        public Message build() {
            Message m = new Message(); m.id=id; m.expediteur=expediteur;
            m.destinataire=destinataire; m.contenu=contenu; m.lu=lu;
            m.delivered=delivered;
            m.conversationId=conversationId; m.luAt=luAt; m.deliveredAt=deliveredAt; return m;
        }
    }
}
