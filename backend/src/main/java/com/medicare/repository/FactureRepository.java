package com.medicare.repository;

import com.medicare.entity.Facture;
import com.medicare.entity.Patient;
import com.medicare.entity.StatutPaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    Page<Facture> findByPatientOrderByDateFactureDesc(Patient patient, Pageable pageable);

    List<Facture> findByStatutPaiement(StatutPaiement statut);

    @Query("SELECT f FROM Facture f WHERE f.statutPaiement IN ('EN_ATTENTE', 'PARTIELLEMENT_PAYE') " +
           "AND f.dateEcheance < :date")
    List<Facture> findFacturesImpayeesEnRetard(@Param("date") LocalDate date);

    @Query("SELECT SUM(f.montantTotal) FROM Facture f WHERE f.dateFacture BETWEEN :debut AND :fin")
    Double sumMontantTotalBetween(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT SUM(f.montantPaye) FROM Facture f WHERE f.dateFacture BETWEEN :debut AND :fin")
    Double sumMontantPayeBetween(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    long countByStatutPaiement(StatutPaiement statut);

    @Query("SELECT FUNCTION('MONTH', f.dateFacture), FUNCTION('YEAR', f.dateFacture), " +
           "SUM(f.montantTotal), COUNT(f) FROM Facture f " +
           "WHERE f.dateFacture BETWEEN :debut AND :fin " +
           "GROUP BY FUNCTION('MONTH', f.dateFacture), FUNCTION('YEAR', f.dateFacture) " +
           "ORDER BY FUNCTION('YEAR', f.dateFacture), FUNCTION('MONTH', f.dateFacture)")
    List<Object[]> revenusMensuels(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
