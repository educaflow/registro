#!/usr/bin/env bash
set -euo pipefail

# Script helper para arrancar PostgreSQL en Docker (si no existe) y ejecutar la aplicación Spring Boot
# Uso:
#   chmod +x scripts/run-local.sh
#   ./scripts/run-local.sh

APP_DB_NAME=educaflow
APP_DB_USER=educaflow
APP_DB_PASS=educaflow
DB_CONTAINER_NAME=educaflow-db
DB_IMAGE=postgres:15
DB_PORT=5432
APP_PORT=8081

echo "Comprobando Java..."
if ! command -v java >/dev/null 2>&1; then
  echo "ERROR: java no está instalado o no está en PATH. Instala Java 21 (OpenJDK) y vuelve a intentarlo." >&2
  exit 1
fi
java -version 2>&1 | sed -n '1p'

# Comprueba si Docker está disponible
if command -v docker >/dev/null 2>&1; then
  echo "Docker detectado. Comprobando contenedor de base de datos..."
  if ! docker ps --format '{{.Names}}' | grep -q "^${DB_CONTAINER_NAME}$"; then
    if docker ps -a --format '{{.Names}}' | grep -q "^${DB_CONTAINER_NAME}$"; then
      echo "Contenedor ${DB_CONTAINER_NAME} existe pero no está en ejecución. Arrancando..."
      docker start ${DB_CONTAINER_NAME}
    else
      echo "Creando y arrancando contenedor PostgreSQL '${DB_CONTAINER_NAME}' (imagen ${DB_IMAGE})..."
      docker run --name ${DB_CONTAINER_NAME} -e POSTGRES_USER=${APP_DB_USER} -e POSTGRES_PASSWORD=${APP_DB_PASS} -e POSTGRES_DB=${APP_DB_NAME} -p ${DB_PORT}:5432 -d ${DB_IMAGE}
    fi
  else
    echo "Contenedor ${DB_CONTAINER_NAME} ya está en ejecución."
  fi

  echo "Esperando a que PostgreSQL acepte conexiones..."
  # Espera hasta que psql responda. Usa psql dentro del contenedor (más fiable que desde host si no tienes psql instalado).
  until docker exec ${DB_CONTAINER_NAME} pg_isready -U ${APP_DB_USER} >/dev/null 2>&1; do
    printf '.'; sleep 1
  done
  echo
  echo "PostgreSQL listo."
else
  echo "Docker NO detectado. Asegúrate de tener PostgreSQL en localhost:5432 con la base de datos, usuario y contraseña configurados en 'application.properties'." >&2
  echo "Puedes crear la BD y el usuario con psql (ejemplo):"
  echo "  sudo -u postgres psql -c \"CREATE USER ${APP_DB_USER} WITH PASSWORD '${APP_DB_PASS}';\""
  echo "  sudo -u postgres psql -c \"CREATE DATABASE ${APP_DB_NAME} OWNER ${APP_DB_USER};\""
fi

# Exportar variables de entorno para sobreescribir application.properties si se desea
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:${DB_PORT}/${APP_DB_NAME}"
export SPRING_DATASOURCE_USERNAME=${APP_DB_USER}
export SPRING_DATASOURCE_PASSWORD=${APP_DB_PASS}
export SERVER_PORT=${APP_PORT}

echo "Variables de entorno exportadas:"
echo "  SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}"
echo "  SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}"
echo "  SERVER_PORT=${SERVER_PORT}"

echo "Arrancando la aplicación con ./mvnw spring-boot:run (salida en la terminal)..."
./mvnw spring-boot:run

# Si prefieres ejecutar el JAR empaquetado:
# ./mvnw package
# java -jar target/registro-0.0.1-SNAPSHOT.jar

