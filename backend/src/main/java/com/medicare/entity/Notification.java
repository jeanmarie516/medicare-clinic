package com.medicare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(length = 50) private String type;
    @Column(nullable = false, length = 200) private String titre;
    @Column(columnDefinition = "TEXT") private String contenu;
    @Column(nullable = false) private Boolean lu = false;
    @Column(length = 255) private String lien;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    public Notification() {}
    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getType() { return type; }
    public String getTitre() { return titre; }
    public String getContenu() { return contenu; }
    public Boolean getLu() { return lu; }
    public String getLien() { return lien; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long v) { this.id=v; }
    public void setUser(User v) { this.user=v; }
    public void setType(String v) { this.type=v; }
    public void setTitre(String v) { this.titre=v; }
    public void setContenu(String v) { this.contenu=v; }
    public void setLu(Boolean v) { this.lu=v; }
    public void setLien(String v) { this.lien=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private User user; private String type, titre, contenu, lien;
        private Boolean lu = false;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder user(User v) { this.user=v; return this; }
        public Builder type(String v) { this.type=v; return this; }
        public Builder titre(String v) { this.titre=v; return this; }
        public Builder contenu(String v) { this.contenu=v; return this; }
        public Builder lu(Boolean v) { this.lu=v; return this; }
        public Builder lien(String v) { this.lien=v; return this; }
        public Notification build() {
            Notification n = new Notification(); n.id=id; n.user=user; n.type=type;
            n.titre=titre; n.contenu=contenu; n.lu=lu; n.lien=lien; return n;
        }
    }
}
