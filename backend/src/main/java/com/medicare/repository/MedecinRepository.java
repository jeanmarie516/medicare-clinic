package com.medicare.repository;

import com.medicare.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {

    List<Medecin> findByDisponibleTrue();

    List<Medecin> findBySpecialiteContainingIgnoreCase(String specialite);

    @Query("SELECT m FROM Medecin m WHERE m.disponible = true AND " +
           "LOWER(m.specialite) LIKE LOWER(CONCAT('%', :specialite, '%'))")
    List<Medecin> findDisponiblesBySpecialite(@Param("specialite") String specialite);

    @Query("SELECT m FROM Medecin m WHERE LOWER(m.user.nom) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.user.prenom) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.specialite) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Medecin> search(@Param("query") String query);

    @Query("SELECT DISTINCT m.specialite FROM Medecin m ORDER BY m.specialite")
    List<String> findAllSpecialites();

    Optional<Medecin> findByUserId(Long userId);
}
