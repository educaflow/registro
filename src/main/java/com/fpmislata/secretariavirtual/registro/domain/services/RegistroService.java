package com.fpmislata.secretariavirtual.registro.domain.services;

import com.fpmislata.secretariavirtual.registro.domain.model.RegistroForm;
import com.fpmislata.secretariavirtual.registro.persistence.entity.Centro;
import com.fpmislata.secretariavirtual.registro.persistence.entity.TipoUsuario;

import java.util.List;

public interface RegistroService {

    /** Comprueba si el documento ya existe en el centro. Lanza excepción si ya está registrado. */
    void verificarDocumento(String documento, Long centroId);

    /** Busca en auth_user_registry los tipos de usuario sugeridos para el documento y centro. */
    RegistryLookupResult lookupDocumento(String documento, Long centroId);

    /** Guarda el nuevo usuario en las tablas de Axelor. */
    void registrar(RegistroForm form);

    List<Centro> getCentros();

    List<TipoUsuario> getTiposUsuario();
}
