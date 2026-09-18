package com.medicare.repository;

import com.medicare.entity.Medecin;
import com.medicare.entity.Patient;
import com.medicare.entity.RendezVous;
import com.medicare.entity.StatutRendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByMedecinAndDateHeureBetweenOrderByDateHeure(
            Medecin medecin, LocalDateTime debut, LocalDateTime fin);

    List<RendezVous> findByPatientAndDateHeureBetweenOrderByDateHeure(
            Patient patient, LocalDateTime debut, LocalDateTime fin);

    Page<RendezVous> findByMedecin(Medecin medecin, Pageable pageable);

    Page<RendezVous> findByPatient(Patient patient, Pageable pageable);

    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin ORDER BY r.dateHeure")
    List<RendezVous> findRendezVousBetween(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM RendezVous r WHERE LOWER(CONCAT(r.patient.nom, ' ', r.patient.prenom)) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<RendezVous> findByPatientNomContainingIgnoreCase(
            @Param("search") String search, Pageable pageable);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin = :medecin AND r.statut = :statut AND r.dateHeure >= :date ORDER BY r.dateHeure")
    List<RendezVous> findMedecinRendezVousByStatutAfter(
            @Param("medecin") Medecin medecin,
            @Param("statut") StatutRendezVous statut,
            @Param("date") LocalDateTime date);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin = :medecin AND r.dateHeure BETWEEN :debut AND :fin AND r.id != :excludedId")
    List<RendezVous> findConflictsForUpdate(
            @Param("medecin") Medecin medecin,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("excludedId") Long excludedId);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin")
    long countRendezVousBetween(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.medecin = :medecin AND r.dateHeure BETWEEN :debut AND :fin")
    long countRendezVousByMedecinBetween(
            @Param("medecin") Medecin medecin,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin AND " +
           "(:medecinId IS NULL OR r.medecin.id = :medecinId) AND " +
           "(:statut IS NULL OR r.statut = :statut) " +
           "ORDER BY r.dateHeure")
    List<RendezVous> findWithFilters(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("medecinId") Long medecinId,
            @Param("statut") StatutRendezVous statut);

    @Query("SELECT FUNCTION('DATE', r.dateHeure), COUNT(r) FROM RendezVous r " +
           "WHERE r.dateHeure BETWEEN :debut AND :fin " +
           "GROUP BY FUNCTION('DATE', r.dateHeure) ORDER BY FUNCTION('DATE', r.dateHeure)")
    List<Object[]> countRendezVousByDay(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
