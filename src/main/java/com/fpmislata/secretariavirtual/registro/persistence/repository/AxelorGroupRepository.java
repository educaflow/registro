package com.fpmislata.secretariavirtual.registro.persistence.repository;

import com.fpmislata.secretariavirtual.registro.persistence.entity.AxelorGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AxelorGroupRepository extends JpaRepository<AxelorGroup, Long> {

    Optional<AxelorGroup> findByCode(String code);
}
