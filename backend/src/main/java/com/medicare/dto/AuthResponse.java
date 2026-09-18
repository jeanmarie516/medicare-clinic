package com.medicare.dto;

import com.medicare.entity.Role;

public class AuthResponse {
    private Long id; private String token; private String refreshToken; private String email;
    private String nom; private String prenom; private Role role; private String photoUrl; private String message;

    public AuthResponse() {}

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public String getToken() { return token; }
    public void setToken(String v) { this.token = v; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String v) { this.refreshToken = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getNom() { return nom; }
    public void setNom(String v) { this.nom = v; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String v) { this.prenom = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String v) { this.photoUrl = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String token, refreshToken, email, nom, prenom, photoUrl, message;
        private Role role;
        Builder() {}
        public Builder id(Long v) { this.id=v; return this; }
        public Builder token(String v) { this.token=v; return this; }
        public Builder refreshToken(String v) { this.refreshToken=v; return this; }
        public Builder email(String v) { this.email=v; return this; }
        public Builder nom(String v) { this.nom=v; return this; }
        public Builder prenom(String v) { this.prenom=v; return this; }
        public Builder role(Role v) { this.role=v; return this; }
        public Builder photoUrl(String v) { this.photoUrl=v; return this; }
        public Builder message(String v) { this.message=v; return this; }
        public AuthResponse build() {
            AuthResponse r = new AuthResponse(); r.id=id; r.token=token; r.refreshToken=refreshToken;
            r.email=email; r.nom=nom; r.prenom=prenom; r.role=role; r.photoUrl=photoUrl; r.message=message; return r;
        }
    }
}
