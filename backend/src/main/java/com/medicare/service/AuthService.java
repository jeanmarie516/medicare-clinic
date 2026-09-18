package com.medicare.service;

import com.medicare.dto.AuthResponse;
import com.medicare.dto.LoginRequest;
import com.medicare.dto.RegisterRequest;
import com.medicare.entity.Medecin;
import com.medicare.entity.Role;
import com.medicare.entity.User;
import com.medicare.exception.BadRequestException;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.MedecinRepository;
import com.medicare.repository.UserRepository;
import com.medicare.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {


    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, MedecinRepository medecinRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, AuditService auditService) {
        this.userRepository = userRepository;
        this.medecinRepository = medecinRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.auditService = auditService;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Email ou mot de passe incorrect");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé"));

        if (!user.getActif()) {
            throw new BadRequestException("Ce compte est désactivé. Veuillez contacter l'administrateur.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        auditService.log("CONNEXION", "UTILISATEUR", "Connexion de " + user.getEmail(), user);

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .photoUrl(user.getPhotoUrl())
                .message("Connexion réussie")
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un utilisateur avec cet email existe déjà");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("La création d'un compte admin n'est pas autorisée");
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .actif(true)
                .telephone(request.getTelephone())
                .build();

        user = userRepository.save(user);

        if (request.getRole() == Role.MEDECIN && request.getSpecialite() != null) {
            Medecin medecin = Medecin.builder()
                    .user(user)
                    .specialite(request.getSpecialite())
                    .disponible(true)
                    .build();
            medecinRepository.save(medecin);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        auditService.log("CREATION_COMPTE", "UTILISATEUR",
                "Création du compte " + user.getEmail() + " avec rôle " + user.getRole(), user);

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .message("Compte créé avec succès")
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateTokenString(refreshToken)) {
            throw new BadRequestException("Token de rafraîchissement invalide ou expiré");
        }

        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        String newToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .id(user.getId())
                .token(newToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .photoUrl(user.getPhotoUrl())
                .message("Token rafraîchi avec succès")
                .build();
    }

    public AuthResponse getCurrentUser(User user) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .photoUrl(user.getPhotoUrl())
                .message("Utilisateur connecté")
                .build();
    }
}
