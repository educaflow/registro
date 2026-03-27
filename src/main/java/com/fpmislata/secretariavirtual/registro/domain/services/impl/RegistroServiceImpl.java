package com.fpmislata.secretariavirtual.registro.domain.services.impl;

import com.fpmislata.secretariavirtual.registro.domain.exception.DocumentoAlreadyRegisteredException;
import com.fpmislata.secretariavirtual.registro.domain.exception.EmailAlreadyExistsException;
import com.fpmislata.secretariavirtual.registro.domain.model.RegistroForm;
import com.fpmislata.secretariavirtual.registro.domain.services.RegistroService;
import com.fpmislata.secretariavirtual.registro.domain.services.RegistryLookupResult;
import com.fpmislata.secretariavirtual.registro.persistence.entity.*;
import com.fpmislata.secretariavirtual.registro.persistence.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistroServiceImpl implements RegistroService {

    private final AxelorUserRepository axelorUserRepository;
    private final AxelorGroupRepository axelorGroupRepository;
    private final CentroRepository centroRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final CentroUsuarioRepository centroUsuarioRepository;
    private final CentroUsuarioTipoUsuarioRepository centroUsuarioTipoUsuarioRepository;
    private final AuthUserRegistryRepository authUserRegistryRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroServiceImpl(
            AxelorUserRepository axelorUserRepository,
            AxelorGroupRepository axelorGroupRepository,
            CentroRepository centroRepository,
            TipoUsuarioRepository tipoUsuarioRepository,
            CentroUsuarioRepository centroUsuarioRepository,
            CentroUsuarioTipoUsuarioRepository centroUsuarioTipoUsuarioRepository,
            AuthUserRegistryRepository authUserRegistryRepository,
            PasswordEncoder passwordEncoder) {
        this.axelorUserRepository = axelorUserRepository;
        this.axelorGroupRepository = axelorGroupRepository;
        this.centroRepository = centroRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.centroUsuarioRepository = centroUsuarioRepository;
        this.centroUsuarioTipoUsuarioRepository = centroUsuarioTipoUsuarioRepository;
        this.authUserRegistryRepository = authUserRegistryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void verificarDocumento(String documento, Long centroId) {
        if (centroUsuarioRepository.existsByCentroIdAndUsuarioDni(centroId, documento)) {
            throw new DocumentoAlreadyRegisteredException(documento);
        }
    }

    @Override
    public RegistryLookupResult lookupDocumento(String documento, Long centroId) {
        Centro centro = centroRepository.findById(centroId).orElse(null);
        if (centro == null) {
            return new RegistryLookupResult(false, false, List.of(), null);
        }

        List<AuthUserRegistry> todos = authUserRegistryRepository.findByCentroAndDni(centro, documento);
        if (todos.isEmpty()) {
            return new RegistryLookupResult(false, false, List.of(),
                    "DNI no encontrado en el registro del centro.");
        }

        // Registros del curso actual
        List<AuthUserRegistry> delCursoActual = todos.stream()
                .filter(r -> r.getCurso() != null && r.getCurso().equals(centro.getCurso()))
                .toList();

        if (!delCursoActual.isEmpty()) {
            List<Long> ids = delCursoActual.stream()
                    .map(r -> r.getTipoUsuario().getId()).distinct().toList();
            String tipos = delCursoActual.stream()
                    .map(r -> r.getTipoUsuario().getName()).distinct()
                    .collect(java.util.stream.Collectors.joining(", "));
            return new RegistryLookupResult(true, true, ids,
                    "DNI encontrado en el registro del curso actual. Tipo de usuario: " + tipos + ".");
        }

        // Registros de cursos anteriores → mapear a ex-variantes
        List<String> exCodes = todos.stream()
                .map(r -> "EX" + r.getTipoUsuario().getCode())
                .distinct().toList();
        List<TipoUsuario> exTipos = tipoUsuarioRepository.findByCodeIn(exCodes);
        List<Long> ids = exTipos.stream().map(TipoUsuario::getId).toList();
        String tipos = exTipos.stream().map(TipoUsuario::getName).distinct()
                .collect(java.util.stream.Collectors.joining(", "));
        return new RegistryLookupResult(true, false, ids,
                "DNI encontrado en el registro de un curso anterior. Tipo de usuario: " + tipos + ".");
    }

    @Override
    @Transactional
    public void registrar(RegistroForm form) {
        // 1. Verificar email único
        if (axelorUserRepository.existsByCode(form.getEmail())) {
            throw new EmailAlreadyExistsException(form.getEmail());
        }

        // 2. Buscar grupo "users"
        AxelorGroup group = axelorGroupRepository.findByCode("users").orElse(null);

        // 3. Buscar centro
        Centro centro = centroRepository.findById(form.getCentroId())
                .orElseThrow(() -> new IllegalArgumentException("Centro no encontrado: " + form.getCentroId()));

        // 4. Guardar usuario Axelor
        AxelorUser user = new AxelorUser();
        user.setCode(form.getEmail());
        user.setName(form.getNombre() + " " + form.getApellidos());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEmail(form.getEmail());
        user.setDni(form.getDocumento());
        user.setNombre(form.getNombre());
        user.setApellidos(form.getApellidos());
        user.setLanguage("es");
        user.setGroup(group);
        user.setCentroActivo(centro);
        user = axelorUserRepository.save(user);

        // 5. Guardar CentroUsuario
        CentroUsuario centroUsuario = new CentroUsuario();
        centroUsuario.setCentro(centro);
        centroUsuario.setUsuario(user);
        centroUsuario = centroUsuarioRepository.save(centroUsuario);

        // 6. Guardar CentroUsuarioTipoUsuario para cada tipo seleccionado
        for (Long tipoId : form.getTiposUsuarioIds()) {
            TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(tipoId)
                    .orElseThrow(() -> new IllegalArgumentException("TipoUsuario no encontrado: " + tipoId));
            CentroUsuarioTipoUsuario cutu = new CentroUsuarioTipoUsuario();
            cutu.setCentroUsuario(centroUsuario);
            cutu.setTipoUsuario(tipoUsuario);
            centroUsuarioTipoUsuarioRepository.save(cutu);
        }
    }

    @Override
    public List<Centro> getCentros() {
        return centroRepository.findAll();
    }

    @Override
    public List<TipoUsuario> getTiposUsuario() {
        return tipoUsuarioRepository.findByCodeIn(List.of("PROFESOR", "ALUMNO", "EXPROFESOR", "EXALUMNO"));
    }
}
