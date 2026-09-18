package com.medicare.repository;

import com.medicare.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.telephone LIKE CONCAT('%', :query, '%')")
    List<Patient> search(@Param("query") String query);

    @Query("SELECT p FROM Patient p ORDER BY p.createdAt DESC")
    List<Patient> findRecentPatients(Pageable pageable);

    long countBySexe(String sexe);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.dateNaissance >= :date")
    long countPatientsByDateNaissanceAfter(@Param("date") java.time.LocalDate date);
}
