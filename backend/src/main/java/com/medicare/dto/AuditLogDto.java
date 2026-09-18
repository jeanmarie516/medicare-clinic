package com.medicare.dto;

import com.medicare.entity.AuditLog;
import java.time.LocalDateTime;

public class AuditLogDto {
    private Long id;
    private String userNom;
    private String userPrenom;
    private String userEmail;
    private String action;
    private String ressource;
    private String detail;
    private String ip;
    private LocalDateTime createdAt;

    public AuditLogDto() {}

    public AuditLogDto(AuditLog log) {
        this.id = log.getId();
        if (log.getUser() != null) {
            this.userNom = log.getUser().getNom();
            this.userPrenom = log.getUser().getPrenom();
            this.userEmail = log.getUser().getEmail();
        } else {
            this.userNom = "Système";
            this.userPrenom = "";
            this.userEmail = "";
        }
        this.action = log.getAction();
        this.ressource = log.getRessource();
        this.detail = log.getDetail();
        this.ip = log.getIp();
        this.createdAt = log.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getUserNom() { return userNom; }
    public String getUserPrenom() { return userPrenom; }
    public String getUserEmail() { return userEmail; }
    public String getAction() { return action; }
    public String getRessource() { return ressource; }
    public String getDetail() { return detail; }
    public String getIp() { return ip; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long v) { this.id=v; }
    public void setUserNom(String v) { this.userNom=v; }
    public void setUserPrenom(String v) { this.userPrenom=v; }
    public void setUserEmail(String v) { this.userEmail=v; }
    public void setAction(String v) { this.action=v; }
    public void setRessource(String v) { this.ressource=v; }
    public void setDetail(String v) { this.detail=v; }
    public void setIp(String v) { this.ip=v; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt=v; }
}
