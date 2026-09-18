package com.medicare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false, length = 100) private String action;
    @Column(nullable = false, length = 100) private String ressource;
    @Column(columnDefinition = "TEXT") private String detail;
    @Column(length = 50) private String ip;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;

    public AuditLog() {}
    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getAction() { return action; }
    public String getRessource() { return ressource; }
    public String getDetail() { return detail; }
    public String getIp() { return ip; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long v) { this.id=v; }
    public void setUser(User v) { this.user=v; }
    public void setAction(String v) { this.action=v; }
    public void setRessource(String v) { this.ressource=v; }
    public void setDetail(String v) { this.detail=v; }
    public void setIp(String v) { this.ip=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private User user; private String action, ressource, detail, ip;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder user(User v) { this.user=v; return this; }
        public Builder action(String v) { this.action=v; return this; }
        public Builder ressource(String v) { this.ressource=v; return this; }
        public Builder detail(String v) { this.detail=v; return this; }
        public Builder ip(String v) { this.ip=v; return this; }
        public AuditLog build() {
            AuditLog a = new AuditLog(); a.id=id; a.user=user; a.action=action;
            a.ressource=ressource; a.detail=detail; a.ip=ip; return a;
        }
    }
}
