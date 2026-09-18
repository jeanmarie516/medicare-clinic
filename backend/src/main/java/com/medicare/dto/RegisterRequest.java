package com.medicare.dto;

import com.medicare.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank @Size(min = 2, max = 50) private String nom;
    @NotBlank @Size(min = 2, max = 50) private String prenom;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6, max = 100) private String password;
    @NotNull private Role role;
    private String telephone;
    private String specialite;

    public RegisterRequest() {}

    public String getNom() { return nom; }
    public void setNom(String v) { this.nom = v; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String v) { this.prenom = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String v) { this.telephone = v; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String v) { this.specialite = v; }
}
