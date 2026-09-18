package com.medicare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    @NotNull private Long expediteurId;
    private String expediteurNom; private String expediteurPrenom;
    @NotNull private Long destinataireId;
    private String destinataireNom; private String destinatairePrenom;
    @NotBlank private String contenu;
    private Boolean lu; private Boolean delivered;
    private String conversationId;
    private LocalDateTime createdAt; private LocalDateTime luAt; private LocalDateTime deliveredAt;

    public MessageDto() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id=v; }
    public Long getExpediteurId() { return expediteurId; }
    public void setExpediteurId(Long v) { this.expediteurId=v; }
    public String getExpediteurNom() { return expediteurNom; }
    public void setExpediteurNom(String v) { this.expediteurNom=v; }
    public String getExpediteurPrenom() { return expediteurPrenom; }
    public void setExpediteurPrenom(String v) { this.expediteurPrenom=v; }
    public Long getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Long v) { this.destinataireId=v; }
    public String getDestinataireNom() { return destinataireNom; }
    public void setDestinataireNom(String v) { this.destinataireNom=v; }
    public String getDestinatairePrenom() { return destinatairePrenom; }
    public void setDestinatairePrenom(String v) { this.destinatairePrenom=v; }
    public String getContenu() { return contenu; }
    public void setContenu(String v) { this.contenu=v; }
    public Boolean getLu() { return lu; }
    public void setLu(Boolean v) { this.lu=v; }
    public Boolean getDelivered() { return delivered; }
    public void setDelivered(Boolean v) { this.delivered=v; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String v) { this.conversationId=v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt=v; }
    public LocalDateTime getLuAt() { return luAt; }
    public void setLuAt(LocalDateTime v) { this.luAt=v; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime v) { this.deliveredAt=v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id, expediteurId, destinataireId;
        private String expediteurNom, expediteurPrenom, destinataireNom, destinatairePrenom;
        private String contenu; private Boolean lu; private Boolean delivered;
        private String conversationId;
        private LocalDateTime createdAt, luAt, deliveredAt;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder expediteurId(Long v) { this.expediteurId=v; return this; }
        public Builder expediteurNom(String v) { this.expediteurNom=v; return this; }
        public Builder expediteurPrenom(String v) { this.expediteurPrenom=v; return this; }
        public Builder destinataireId(Long v) { this.destinataireId=v; return this; }
        public Builder destinataireNom(String v) { this.destinataireNom=v; return this; }
        public Builder destinatairePrenom(String v) { this.destinatairePrenom=v; return this; }
        public Builder contenu(String v) { this.contenu=v; return this; }
        public Builder lu(Boolean v) { this.lu=v; return this; }
        public Builder delivered(Boolean v) { this.delivered=v; return this; }
        public Builder conversationId(String v) { this.conversationId=v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt=v; return this; }
        public Builder luAt(LocalDateTime v) { this.luAt=v; return this; }
        public Builder deliveredAt(LocalDateTime v) { this.deliveredAt=v; return this; }
        public MessageDto build() {
            MessageDto d = new MessageDto(); d.id=id; d.expediteurId=expediteurId;
            d.expediteurNom=expediteurNom; d.expediteurPrenom=expediteurPrenom;
            d.destinataireId=destinataireId; d.destinataireNom=destinataireNom;
            d.destinatairePrenom=destinatairePrenom; d.contenu=contenu; d.lu=lu;
            d.delivered=delivered;
            d.conversationId=conversationId; d.createdAt=createdAt;
            d.luAt=luAt; d.deliveredAt=deliveredAt; return d;
        }
    }
}
