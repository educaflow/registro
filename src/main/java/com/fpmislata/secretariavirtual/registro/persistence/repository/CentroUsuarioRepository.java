package com.fpmislata.secretariavirtual.registro.persistence.repository;

import com.fpmislata.secretariavirtual.registro.persistence.entity.CentroUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CentroUsuarioRepository extends JpaRepository<CentroUsuario, Long> {

    boolean existsByCentroIdAndUsuarioDni(Long centroId, String dni);
}
