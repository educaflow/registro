# Registro de usuarios — Secretaría Virtual

Aplicación web de autoregistro para alumnos y profesores de un centro educativo. Crea usuarios directamente en la base de datos de **Axelor** (ERP del centro), permitiendo que los nuevos usuarios accedan a la secretaría virtual sin intervención manual del administrador.

## Tecnologías

- Java 21 + Spring Boot 4
- Thymeleaf + Bootstrap 5
- Spring Data JPA + PostgreSQL
- BouncyCastle (Argon2id, compatible con Apache Shiro 2 / Axelor)

## Requisitos previos

- Java 21
- Maven 3.8+
- PostgreSQL 12+ en `localhost:5432`, base de datos `educaflow`, usuario `educaflow`, contraseña `educaflow` (la misma BD de la aplicación Axelor)

## Arrancar en local

Con Docker disponible, el script levanta el contenedor de BD si no está activo y arranca la aplicación:

```bash
chmod +x scripts/run-local.sh
./scripts/run-local.sh
```

O manualmente:

```bash
mvn spring-boot:run
```

La aplicación queda disponible en `http://localhost:8081/registro`.

## Flujo de registro

El proceso se divide en dos pasos:

**Paso 1 — Verificación de documento**

El usuario elige el tipo de documento (DNI/NIE u otro), escribe su número y selecciona el centro. La aplicación comprueba que ese documento no esté ya registrado en el centro.

**Paso 2 — Datos personales**

El usuario introduce nombre, apellidos, email (que será su usuario de acceso), tipo de usuario y contraseña. Si el documento aparece en el registro previo del centro (`security_auth_user_registry`), el tipo de usuario se preselecciona automáticamente:

| Situación | Tipo preseleccionado |
|---|---|
| Documento en el registro del **curso actual** | Profesor / Alumno |
| Documento en el registro de un **curso anterior** | Exprofesor / Exalumno |
| Documento no encontrado | Sin preselección |

Una alerta informativa en esta pantalla indica el resultado de la búsqueda.

Al confirmar, se crean las siguientes filas en la BD de Axelor:

- `auth_user` — con contraseña Argon2id (formato Shiro 2), idioma español, centro activo
- `security_security_actor` + `security_centro_usuario` — vincula usuario y centro
- `security_centro_usuario_tipo_usuario` — uno por cada tipo de usuario seleccionado

## Configuración

`src/main/resources/application.properties`:

```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/educaflow
spring.datasource.username=educaflow
spring.datasource.password=educaflow
app.centro.code=MISLATA   # código del centro en common_centro (actualmente no usado para filtrar, reservado)
```

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| `GET` | `/registro` | Paso 1: formulario de documento |
| `POST` | `/registro/verificar` | Valida documento y redirige al paso 2 |
| `GET` | `/registro/datos` | Paso 2: formulario de datos personales |
| `POST` | `/registro` | Guarda el nuevo usuario |
| `GET` | `/registro/exito` | Pantalla de confirmación |
| `GET` | `/api/lookup/documento?documento=X&centroId=Y` | Consulta asíncrona del registro previo (JSON) |

## Notas sobre la integración con Axelor

- `ddl-auto=none`: la aplicación nunca modifica el esquema de BD.
- Las contraseñas usan **Argon2id** en formato Shiro 2 (`$shiro2$argon2id$...`). No usar BCrypt.
- Las secuencias son por tabla (`auth_user_seq`, `security_security_actor_seq`, etc.), no existe `hibernate_sequence`.
- Los nombres de tabla siguen la convención Axelor: `{módulo}_{entidad_snake_case}`.
- Las columnas FK usan el nombre simple del campo sin sufijo `_id` (e.g., `centro`, `usuario`), excepto `group_id` que viene del modelo base de Axelor.
