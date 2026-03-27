package com.fpmislata.secretariavirtual.registro.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_security_actor")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SecurityActor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "security_actor_seq")
    @SequenceGenerator(name = "security_actor_seq", sequenceName = "security_security_actor_seq", allocationSize = 1)
    private Long id;

    @Column(name = "version")
    private Integer version = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
