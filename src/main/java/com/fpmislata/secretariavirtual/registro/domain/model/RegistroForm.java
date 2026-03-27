package com.fpmislata.secretariavirtual.registro.domain.model;

import java.util.ArrayList;
import java.util.List;

public class RegistroForm {

    private String tipoDocumento;
    private String documento;
    private Long centroId;

    private String nombre;
    private String apellidos;
    private String email;
    private List<Long> tiposUsuarioIds = new ArrayList<>();
    private String password;
    private String confirmPassword;

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public Long getCentroId() { return centroId; }
    public void setCentroId(Long centroId) { this.centroId = centroId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Long> getTiposUsuarioIds() { return tiposUsuarioIds; }
    public void setTiposUsuarioIds(List<Long> tiposUsuarioIds) { this.tiposUsuarioIds = tiposUsuarioIds; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
