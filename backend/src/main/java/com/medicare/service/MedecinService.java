package com.medicare.service;

import com.medicare.dto.MedecinDto;
import com.medicare.entity.Medecin;
import com.medicare.entity.User;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.MedecinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedecinService {


    private final MedecinRepository medecinRepository;
    private final AuditService auditService;

    public MedecinService(MedecinRepository medecinRepository, AuditService auditService) {
        this.medecinRepository = medecinRepository;
        this.auditService = auditService;
    }

    public List<MedecinDto> getAllMedecins() {
        return medecinRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MedecinDto> getMedecinsDisponibles() {
        return medecinRepository.findByDisponibleTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MedecinDto getMedecinById(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'id: " + id));
        return toDto(medecin);
    }

    public MedecinDto getMedecinByUserId(Long userId) {
        Medecin medecin = medecinRepository.findAll().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé pour l'utilisateur: " + userId));
        return toDto(medecin);
    }

    @Transactional
    public MedecinDto updateMedecin(Long id, MedecinDto dto, User user) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'id: " + id));

        if (dto.getSpecialite() != null) medecin.setSpecialite(dto.getSpecialite());
        if (dto.getNumeroOrdre() != null) medecin.setNumeroOrdre(dto.getNumeroOrdre());
        if (dto.getDisponible() != null) medecin.setDisponible(dto.getDisponible());
        if (dto.getTarifConsultation() != null) medecin.setTarifConsultation(dto.getTarifConsultation());
        if (dto.getBiographie() != null) medecin.setBiographie(dto.getBiographie());
        if (dto.getAnneesExperience() != null) medecin.setAnneesExperience(dto.getAnneesExperience());

        medecin = medecinRepository.save(medecin);
        auditService.log("MODIFICATION", "MEDECIN",
                "Modification du médecin " + medecin.getUser().getPrenom() + " " + medecin.getUser().getNom(), user);
        return toDto(medecin);
    }

    public MedecinDto toDto(Medecin medecin) {
        return MedecinDto.builder()
                .id(medecin.getId())
                .userId(medecin.getUser().getId())
                .nom(medecin.getUser().getNom())
                .prenom(medecin.getUser().getPrenom())
                .email(medecin.getUser().getEmail())
                .telephone(medecin.getUser().getTelephone())
                .photoUrl(medecin.getUser().getPhotoUrl())
                .specialite(medecin.getSpecialite())
                .numeroOrdre(medecin.getNumeroOrdre())
                .disponible(medecin.getDisponible())
                .tarifConsultation(medecin.getTarifConsultation())
                .biographie(medecin.getBiographie())
                .anneesExperience(medecin.getAnneesExperience())
                .build();
    }
}
