package com.fpmislata.secretariavirtual.registro.persistence.repository;

import com.fpmislata.secretariavirtual.registro.persistence.entity.AuthUserRegistry;
import com.fpmislata.secretariavirtual.registro.persistence.entity.Centro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthUserRegistryRepository extends JpaRepository<AuthUserRegistry, Long> {

    List<AuthUserRegistry> findByCentroAndDni(Centro centro, String dni);
}
