package com.fpmislata.secretariavirtual.registro.web;

import com.fpmislata.secretariavirtual.registro.domain.exception.DocumentoAlreadyRegisteredException;
import com.fpmislata.secretariavirtual.registro.domain.exception.EmailAlreadyExistsException;
import com.fpmislata.secretariavirtual.registro.domain.model.RegistroForm;
import com.fpmislata.secretariavirtual.registro.domain.model.TipoDocumento;
import com.fpmislata.secretariavirtual.registro.domain.services.RegistroService;
import com.fpmislata.secretariavirtual.registro.domain.services.RegistryLookupResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    /** Paso 1: formulario de verificación de documento */
    @GetMapping
    public String paso1(Model model) {
        model.addAttribute("centros", registroService.getCentros());
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        return "registro/form";
    }

    /** Paso 1 submit: verifica documento y redirige a paso 2 */
    @PostMapping("/verificar")
    public String verificar(
            @RequestParam String tipoDocumento,
            @RequestParam String documento,
            @RequestParam Long centroId,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            registroService.verificarDocumento(documento, centroId);
        } catch (DocumentoAlreadyRegisteredException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("centros", registroService.getCentros());
            model.addAttribute("tiposDocumento", TipoDocumento.values());
            return "registro/form";
        }
        redirectAttributes.addAttribute("documento", documento);
        redirectAttributes.addAttribute("tipoDocumento", tipoDocumento);
        redirectAttributes.addAttribute("centroId", centroId);
        return "redirect:/registro/datos";
    }

    /** Paso 2: formulario con datos del usuario */
    @GetMapping("/datos")
    public String paso2(
            @RequestParam String documento,
            @RequestParam String tipoDocumento,
            @RequestParam Long centroId,
            Model model) {
        RegistryLookupResult lookup = registroService.lookupDocumento(documento, centroId);
        model.addAttribute("tiposUsuario", registroService.getTiposUsuario());
        model.addAttribute("sugeridosTiposUsuarioIds", lookup.tiposUsuarioIds());
        model.addAttribute("lookupMensaje", lookup.mensaje());
        model.addAttribute("lookupFound", lookup.found());
        model.addAttribute("lookupCursoActual", lookup.cursoActual());
        model.addAttribute("documento", documento);
        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("centroId", centroId);
        return "registro/paso2";
    }

    /** Paso 2 submit: guarda el usuario */
    @PostMapping
    public String registrar(
            @ModelAttribute RegistroForm form,
            Model model) {
        if (form.getTiposUsuarioIds() == null || form.getTiposUsuarioIds().isEmpty()) {
            return devolverPaso2ConError(form, model, "Debes seleccionar al menos un tipo de usuario.");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            return devolverPaso2ConError(form, model, "Las contraseñas no coinciden.");
        }
        try {
            registroService.registrar(form);
        } catch (EmailAlreadyExistsException e) {
            return devolverPaso2ConError(form, model, e.getMessage());
        }
        return "redirect:/registro/exito";
    }

    @GetMapping("/exito")
    public String exito() {
        return "registro/success";
    }

    private String devolverPaso2ConError(RegistroForm form, Model model, String error) {
        RegistryLookupResult lookup = registroService.lookupDocumento(form.getDocumento(), form.getCentroId());
        model.addAttribute("error", error);
        model.addAttribute("tiposUsuario", registroService.getTiposUsuario());
        model.addAttribute("sugeridosTiposUsuarioIds", lookup.tiposUsuarioIds());
        model.addAttribute("lookupMensaje", lookup.mensaje());
        model.addAttribute("lookupFound", lookup.found());
        model.addAttribute("lookupCursoActual", lookup.cursoActual());
        model.addAttribute("documento", form.getDocumento());
        model.addAttribute("tipoDocumento", form.getTipoDocumento());
        model.addAttribute("centroId", form.getCentroId());
        model.addAttribute("form", form);
        return "registro/paso2";
    }
}
