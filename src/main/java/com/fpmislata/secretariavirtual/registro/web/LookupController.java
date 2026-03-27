package com.fpmislata.secretariavirtual.registro.web;

import com.fpmislata.secretariavirtual.registro.domain.services.RegistroService;
import com.fpmislata.secretariavirtual.registro.domain.services.RegistryLookupResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lookup")
public class LookupController {

    private final RegistroService registroService;

    public LookupController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping("/documento")
    public LookupResponse lookupDocumento(
            @RequestParam String documento,
            @RequestParam Long centroId) {
        RegistryLookupResult result = registroService.lookupDocumento(documento, centroId);
        return new LookupResponse(result.found(), result.cursoActual(), result.tiposUsuarioIds(), result.mensaje());
    }
}
