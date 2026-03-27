# CLAUDE.md

Este archivo proporciona orientación a Claude Code (claude.ai/code) cuando trabaja con el código en este repositorio.

## Comandos

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=RegistroApplicationTests
```

La aplicación arranca en el puerto **8081**. Requiere una base de datos PostgreSQL en `localhost:5432` con la base de datos `educaflow`, usuario `educaflow`, contraseña `educaflow`. Usar `scripts/run-local.sh` para arrancar el contenedor Docker si no está activo.

## Arquitectura

Aplicación Spring Boot 4 + Thymeleaf + Bootstrap 5 + PostgreSQL para el registro de usuarios en un centro educativo. Escribe directamente en la base de datos de una aplicación existente de **Axelor**. Tres capas:

```
web/  →  domain/services/  →  persistence/
          domain/model/
          domain/exception/
```

### Estructura de paquetes bajo `com.fpmislata.secretariavirtual.registro`

- **`config/`** — `AppConfig`: bean `PasswordEncoder` (Shiro Argon2id); `ShiroArgon2PasswordEncoder`: implementación compatible con Axelor
- **`web/`** — `RegistroController` (flujo 2 pasos) y `LookupController` (REST `/api/lookup/documento`)
- **`domain/model/`** — `RegistroForm` (DTO del formulario), `TipoDocumento` (enum)
- **`domain/exception/`** — `DocumentoAlreadyRegisteredException`, `EmailAlreadyExistsException`
- **`domain/services/`** — interfaz `RegistroService`, record `RegistryLookupResult`; impl en `domain/services/impl/RegistroServiceImpl`
- **`persistence/entity/`** — entidades JPA que mapean tablas de Axelor
- **`persistence/repository/`** — interfaces Spring Data JPA

### Mapeo de la base de datos de Axelor

Esta app NO usa DDL de Hibernate (`ddl-auto=none`). Todas las tablas son propiedad de Axelor. Axelor convierte los nombres de entidad de **camelCase a snake_case**: `{module}_{entity_snake_case}`.

| Entidad JPA | Tabla Axelor |
|---|---|
| `AxelorUser` | `auth_user` |
| `AxelorGroup` | `auth_group` |
| `SecurityActor` | `security_security_actor` |
| `CentroUsuario` | `security_centro_usuario` |
| `TipoUsuario` | `security_tipo_usuario` |
| `CentroUsuarioTipoUsuario` | `security_centro_usuario_tipo_usuario` |
| `Centro` | `common_centro` |
| `AuthUserRegistry` | `security_auth_user_registry` |

`SecurityActor` utiliza herencia JPA **JOINED** — insertar un `CentroUsuario` o `TipoUsuario` escribe una fila en `security_security_actor` y otra en la tabla hija, compartiendo el mismo `id`.

Cada entidad tiene su propia secuencia PostgreSQL (no existe `hibernate_sequence` en esta BD):

| Entidad | Secuencia |
|---|---|
| `AxelorUser` | `auth_user_seq` |
| `SecurityActor` / `CentroUsuario` | `security_security_actor_seq` |
| `CentroUsuarioTipoUsuario` | `security_centro_usuario_tipo_usuario_seq` |

Columnas relevantes de `auth_user` que se rellenan al registrar:

| Campo Java | Columna | Valor |
|---|---|---|
| `code` | `code` | email (login único) |
| `name` | `name` | nombre + apellidos |
| `password` | `password` | hash Argon2id formato Shiro 2 |
| `email` | `email` | email |
| `dni` | `dni` | documento introducido |
| `nombre` | `nombre` | nombre |
| `apellidos` | `apellidos` | apellidos |
| `language` | `language` | `"es"` |
| `group` | `group_id` | FK al grupo con `code = "users"` |
| `centroActivo` | `centro_activo` | FK al centro seleccionado |

Las columnas FK siguen la convención Axelor: nombre simple del campo sin sufijo `_id` (excepto `group_id` que lo define explícitamente el modelo base de Axelor).

### Encriptación de contraseñas

Axelor usa **Apache Shiro 2 + Argon2id**. El formato almacenado es:

```
$shiro2$argon2id$v=19$t=1,m=65536,p=4$<salt_base64>$<hash_base64>
```

Parámetros: iteraciones=1, memoria=65536 KB, paralelismo=4, sal=16 bytes, hash=32 bytes, codificación Base64 sin padding. Implementado en `ShiroArgon2PasswordEncoder` usando BouncyCastle (`bcprov-jdk18on:1.80`). **No usar BCrypt** — Axelor no lo reconoce.

### Flujo de registro

**Paso 1** — `GET /registro`: el usuario elige tipo de documento, escribe el número y selecciona el centro.

**POST /registro/verificar**:
1. Comprueba si ya existe un `CentroUsuario` con ese `dni` en ese centro → error si existe.
2. Redirige a paso 2 con `documento`, `tipoDocumento` y `centroId` como query params.

**Paso 2** — `GET /registro/datos`: busca en `security_auth_user_registry` por centro + DNI:
- Registro con **`curso = centro.curso`** → preselecciona Profesor o Alumno (alerta verde).
- Registro con **curso distinto** → preselecciona Exprofesor o Exalumno (alerta amarilla).
- Sin registro → opciones vacías (alerta gris).

El formulario muestra checkboxes para: **Profesor, Alumno, Exprofesor, Exalumno**.

**POST /registro**:
1. Verifica que el email no esté ya usado como `code` en `auth_user`.
2. Busca el grupo Axelor con `code = "users"`.
3. Guarda `AxelorUser` (contraseña Argon2id, idioma `es`, `centro_activo` = centro elegido).
4. Guarda `CentroUsuario` enlazando usuario ↔ centro.
5. Guarda un `CentroUsuarioTipoUsuario` por cada tipo seleccionado.

**GET /registro/exito**: pantalla de confirmación.

**GET /api/lookup/documento?documento=X&centroId=Y**: endpoint REST para consulta asíncrona del registro, devuelve `LookupResponse` (JSON).
