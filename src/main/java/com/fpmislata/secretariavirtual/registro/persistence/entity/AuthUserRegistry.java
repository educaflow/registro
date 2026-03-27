package com.fpmislata.secretariavirtual.registro.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_auth_user_registry")
public class AuthUserRegistry {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro")
    private Centro centro;

    @Column(name = "curso")
    private Integer curso;

    @Column(name = "dni")
    private String dni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_usuario")
    private TipoUsuario tipoUsuario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Centro getCentro() { return centro; }
    public void setCentro(Centro centro) { this.centro = centro; }

    public Integer getCurso() { return curso; }
    public void setCurso(Integer curso) { this.curso = curso; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}
