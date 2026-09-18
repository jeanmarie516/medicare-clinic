package com.medicare.dto;

import java.time.LocalDateTime;

public class NotificationDto {
    private Long id; private Long userId; private String type;
    private String titre; private String contenu; private Boolean lu;
    private String lien; private LocalDateTime createdAt;

    public NotificationDto() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id=v; }
    public Long getUserId() { return userId; }
    public void setUserId(Long v) { this.userId=v; }
    public String getType() { return type; }
    public void setType(String v) { this.type=v; }
    public String getTitre() { return titre; }
    public void setTitre(String v) { this.titre=v; }
    public String getContenu() { return contenu; }
    public void setContenu(String v) { this.contenu=v; }
    public Boolean getLu() { return lu; }
    public void setLu(Boolean v) { this.lu=v; }
    public String getLien() { return lien; }
    public void setLien(String v) { this.lien=v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, userId; private String type, titre, contenu, lien;
        private Boolean lu; private LocalDateTime createdAt;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder userId(Long v) { this.userId=v; return this; }
        public Builder type(String v) { this.type=v; return this; }
        public Builder titre(String v) { this.titre=v; return this; }
        public Builder contenu(String v) { this.contenu=v; return this; }
        public Builder lu(Boolean v) { this.lu=v; return this; }
        public Builder lien(String v) { this.lien=v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt=v; return this; }
        public NotificationDto build() {
            NotificationDto d = new NotificationDto(); d.id=id; d.userId=userId;
            d.type=type; d.titre=titre; d.contenu=contenu; d.lu=lu; d.lien=lien;
            d.createdAt=createdAt; return d;
        }
    }
}
