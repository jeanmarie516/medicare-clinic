package com.medicare.repository;

import com.medicare.entity.Medecin;
import com.medicare.entity.Patient;
import com.medicare.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Page<Prescription> findByPatientOrderByDatePrescriptionDesc(Patient patient, Pageable pageable);

    List<Prescription> findByPatientOrderByDatePrescriptionDesc(Patient patient);

    Page<Prescription> findByMedecinOrderByDatePrescriptionDesc(Medecin medecin, Pageable pageable);

    long countByPatient(Patient patient);
}
