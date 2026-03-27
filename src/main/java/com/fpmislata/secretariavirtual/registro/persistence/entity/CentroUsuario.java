package com.fpmislata.secretariavirtual.registro.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_centro_usuario")
@PrimaryKeyJoinColumn(name = "id")
public class CentroUsuario extends SecurityActor {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro")
    private Centro centro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario")
    private AxelorUser usuario;

    public Centro getCentro() { return centro; }
    public void setCentro(Centro centro) { this.centro = centro; }

    public AxelorUser getUsuario() { return usuario; }
    public void setUsuario(AxelorUser usuario) { this.usuario = usuario; }
}
