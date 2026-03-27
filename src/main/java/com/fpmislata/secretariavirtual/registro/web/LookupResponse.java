package com.fpmislata.secretariavirtual.registro.web;

import java.util.List;

public record LookupResponse(boolean found, boolean cursoActual, List<Long> tiposUsuarioIds, String mensaje) {
}
