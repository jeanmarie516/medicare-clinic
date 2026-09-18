package com.medicare.repository;

import com.medicare.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Optional<Configuration> findByCle(String cle);

    boolean existsByCle(String cle);

    List<Configuration> findByCleStartingWith(String prefix);
}
