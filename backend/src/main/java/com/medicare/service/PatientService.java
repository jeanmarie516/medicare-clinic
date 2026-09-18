package com.medicare.service;

import com.medicare.dto.PatientDto;
import com.medicare.entity.Patient;
import com.medicare.entity.User;
import com.medicare.exception.BadRequestException;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {


    private final PatientRepository patientRepository;
    private final AuditService auditService;

    public PatientService(PatientRepository patientRepository, AuditService auditService) {
        this.patientRepository = patientRepository;
        this.auditService = auditService;
    }

    public Page<PatientDto> getAllPatients(int page, int size, String search) {
        Page<Patient> patients;
        if (search != null && !search.isEmpty()) {
            patients = patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                    search, search, PageRequest.of(page, size));
        } else {
            patients = patientRepository.findAll(PageRequest.of(page, size));
        }
        return patients.map(this::toDto);
    }

    public List<PatientDto> searchPatients(String query) {
        return patientRepository.search(query).stream()
                .limit(20)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'id: " + id));
        return toDto(patient);
    }

    @Transactional
    public PatientDto createPatient(PatientDto dto, User user) {
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new BadRequestException("Le nom du patient est obligatoire");
        }
        if (dto.getPrenom() == null || dto.getPrenom().isBlank()) {
            throw new BadRequestException("Le prénom du patient est obligatoire");
        }

        Patient patient = Patient.builder()
                .nom(dto.getNom().toUpperCase())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .adresse(dto.getAdresse())
                .groupeSanguin(dto.getGroupeSanguin())
                .allergies(dto.getAllergies())
                .antecedents(dto.getAntecedents())
                .numeroSecuriteSociale(dto.getNumeroSecuriteSociale())
                .profession(dto.getProfession())
                .personneContactNom(dto.getPersonneContactNom())
                .personneContactTelephone(dto.getPersonneContactTelephone())
                .createdBy(user)
                .build();

        patient = patientRepository.save(patient);
        auditService.log("CREATION", "PATIENT", "Création du patient " + patient.getNom() + " " + patient.getPrenom(), user);
        return toDto(patient);
    }

    @Transactional
    public PatientDto updatePatient(Long id, PatientDto dto, User user) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'id: " + id));

        if (dto.getNom() != null) patient.setNom(dto.getNom().toUpperCase());
        if (dto.getPrenom() != null) patient.setPrenom(dto.getPrenom());
        if (dto.getDateNaissance() != null) patient.setDateNaissance(dto.getDateNaissance());
        if (dto.getSexe() != null) patient.setSexe(dto.getSexe());
        if (dto.getTelephone() != null) patient.setTelephone(dto.getTelephone());
        if (dto.getEmail() != null) patient.setEmail(dto.getEmail());
        if (dto.getAdresse() != null) patient.setAdresse(dto.getAdresse());
        if (dto.getGroupeSanguin() != null) patient.setGroupeSanguin(dto.getGroupeSanguin());
        if (dto.getAllergies() != null) patient.setAllergies(dto.getAllergies());
        if (dto.getAntecedents() != null) patient.setAntecedents(dto.getAntecedents());
        if (dto.getNumeroSecuriteSociale() != null) patient.setNumeroSecuriteSociale(dto.getNumeroSecuriteSociale());
        if (dto.getProfession() != null) patient.setProfession(dto.getProfession());
        if (dto.getPersonneContactNom() != null) patient.setPersonneContactNom(dto.getPersonneContactNom());
        if (dto.getPersonneContactTelephone() != null) patient.setPersonneContactTelephone(dto.getPersonneContactTelephone());

        patient = patientRepository.save(patient);
        auditService.log("MODIFICATION", "PATIENT", "Modification du patient " + patient.getNom() + " " + patient.getPrenom(), user);
        return toDto(patient);
    }

    @Transactional
    public void deletePatient(Long id, User user) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'id: " + id));
        patientRepository.delete(patient);
        auditService.log("SUPPRESSION", "PATIENT", "Suppression du patient " + patient.getNom() + " " + patient.getPrenom(), user);
    }

    public PatientDto toDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .nom(patient.getNom())
                .prenom(patient.getPrenom())
                .dateNaissance(patient.getDateNaissance())
                .sexe(patient.getSexe())
                .telephone(patient.getTelephone())
                .email(patient.getEmail())
                .adresse(patient.getAdresse())
                .groupeSanguin(patient.getGroupeSanguin())
                .allergies(patient.getAllergies())
                .antecedents(patient.getAntecedents())
                .numeroSecuriteSociale(patient.getNumeroSecuriteSociale())
                .profession(patient.getProfession())
                .personneContactNom(patient.getPersonneContactNom())
                .personneContactTelephone(patient.getPersonneContactTelephone())
                .createdAt(patient.getCreatedAt())
                .createdById(patient.getCreatedBy().getId())
                .createdByNom(patient.getCreatedBy().getPrenom() + " " + patient.getCreatedBy().getNom())
                .build();
    }
}
