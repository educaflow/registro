package com.fpmislata.secretariavirtual.registro.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_tipo_usuario")
@PrimaryKeyJoinColumn(name = "id")
public class TipoUsuario extends SecurityActor {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
