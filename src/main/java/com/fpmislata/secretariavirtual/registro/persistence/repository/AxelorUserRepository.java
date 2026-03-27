package com.fpmislata.secretariavirtual.registro.persistence.repository;

import com.fpmislata.secretariavirtual.registro.persistence.entity.AxelorUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AxelorUserRepository extends JpaRepository<AxelorUser, Long> {

    boolean existsByCode(String code);

    Optional<AxelorUser> findByCode(String code);
}
