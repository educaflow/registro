package com.fpmislata.secretariavirtual.registro.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "common_centro")
public class Centro {

    @Id
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "curso")
    private Integer curso;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCurso() { return curso; }
    public void setCurso(Integer curso) { this.curso = curso; }
}
