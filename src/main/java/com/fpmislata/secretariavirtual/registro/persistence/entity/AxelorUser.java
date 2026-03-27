package com.fpmislata.secretariavirtual.registro.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_user")
public class AxelorUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "axelor_user_seq")
    @SequenceGenerator(name = "axelor_user_seq", sequenceName = "auth_user_seq", allocationSize = 1)
    private Long id;

    @Column(name = "version")
    private Integer version = 0;

    /** Email usado como login (campo "code" de Axelor) */
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    /** Nombre completo: nombre + apellidos */
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "dni")
    private String dni;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "language")
    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private AxelorGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_activo")
    private Centro centroActivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public AxelorGroup getGroup() { return group; }
    public void setGroup(AxelorGroup group) { this.group = group; }

    public Centro getCentroActivo() { return centroActivo; }
    public void setCentroActivo(Centro centroActivo) { this.centroActivo = centroActivo; }
}
