package com.fpmislata.secretariavirtual.registro.persistence.repository;

import com.fpmislata.secretariavirtual.registro.persistence.entity.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    List<TipoUsuario> findByCodeIn(List<String> codes);
}
