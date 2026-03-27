package com.fpmislata.secretariavirtual.registro.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_centro_usuario_tipo_usuario")
public class CentroUsuarioTipoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cutu_seq")
    @SequenceGenerator(name = "cutu_seq", sequenceName = "security_centro_usuario_tipo_usuario_seq", allocationSize = 1)
    private Long id;

    @Column(name = "version")
    private Integer version = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_usuario")
    private CentroUsuario centroUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_usuario")
    private TipoUsuario tipoUsuario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public CentroUsuario getCentroUsuario() { return centroUsuario; }
    public void setCentroUsuario(CentroUsuario centroUsuario) { this.centroUsuario = centroUsuario; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}
