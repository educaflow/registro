package com.fpmislata.secretariavirtual.registro.persistence.repository;

import com.fpmislata.secretariavirtual.registro.persistence.entity.Centro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CentroRepository extends JpaRepository<Centro, Long> {

    Optional<Centro> findByCode(String code);
}
