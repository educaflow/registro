package com.fpmislata.secretariavirtual.registro.domain.services;

import java.util.List;

public record RegistryLookupResult(boolean found, boolean cursoActual, List<Long> tiposUsuarioIds, String mensaje) {
}
