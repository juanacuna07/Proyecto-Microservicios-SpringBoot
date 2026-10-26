# Proyecto 03 – Aplicación Multicontenedor con Docker Compose

**Universidad de Ibagué – Infraestructura y Seguridad Computacional**
Aplicación distribuida con arquitectura de microservicios: Spring Boot + API REST + JWT (RSA) + PostgreSQL + Docker Compose + Swagger + CI/CD.

## Integrantes

| Integrante | Rol en el video |
|---|---|
| Juan Camilo Acuña | Arquitectura y servicios del `docker-compose.yml` |
| Luis Miguel Hernández | Enlaces entre servicios y volúmenes |
| Stephany Trujillo | Puertos, redes y pruebas funcionales (Postman) |
| Daniel Arteaga | Publicación en Docker Hub y GitHub |

## Arquitectura

```
                Cliente (Postman / navegador)
                            │
                ┌───────────▼───────────┐
                │  api-gateway  :8080   │   frontend-net
                └─────┬───────────┬─────┘
                      │           │
         ┌────────────▼──┐     ┌──▼────────────┐
         │ auth-service  │     │ user-service  │   frontend-net + backend-net
         │ :8081 BCrypt  │     │ :8082 CRUD    │
         │ firma JWT RSA │     │ valida JWT    │
         └──────┬────────┘     └───────┬───────┘
                │                      │
         ┌──────▼──────┐        ┌──────▼──────┐
         │  auth-db    │        │  users-db   │   backend-net
         │ PostgreSQL  │        │ PostgreSQL  │
         │   :5433     │        │   :5434     │
         └─────────────┘        └─────────────┘

     key-generator (proceso único): crea el par de llaves RSA en los volúmenes jwt-private / jwt-public
```

| Componente | Puerto | Responsabilidad | Datos |
|---|---|---|---|
| api-gateway | 8080 | Enrutamiento y Correlation ID | Sin base de datos |
| auth-service | 8081 | Registro, login, BCrypt, emisión JWT | auth_db |
| user-service | 8082 | CRUD de perfiles y autorización | users_db |
| auth-db | 5433 | PostgreSQL 17 | volumen `auth-data` |
| users-db | 5434 | PostgreSQL 17 | volumen `users-data` |
| key-generator | – | Genera llaves RSA (procesamiento) | volúmenes `jwt-private`, `jwt-public` |

## Cumplimiento de la rúbrica (docker-compose.yml)

| Criterio | Dónde se ve |
|---|---|
| 1. Archivo `.yml` correcto | `docker-compose.yml` en la raíz |
| 2. Servicios (mín. 3) | 6 servicios: `auth-db`, `users-db`, `key-generator`, `auth-service`, `user-service`, `api-gateway` |
| 3. Enlaces entre servicios | `depends_on` con condiciones (`service_healthy`, `service_completed_successfully`), `links`, y URLs internas por nombre de servicio (`auth-db:5432`, `http://auth-service:8081`) |
| 4. Volúmenes (mín. 3) | 4 volúmenes con nombre: `auth-data`, `users-data`, `jwt-private`, `jwt-public` |
| 5. Puertos | 8080, 8081, 8082, 5433, 5434 |
| 6. Redes | `frontend-net` (gateway ↔ servicios) y `backend-net` (servicios ↔ bases de datos) |
| 7. Docker Hub y GitHub | `scripts/publish-dockerhub.sh` + workflow `.github/workflows/ci-cd.yml` |

## Requisitos

- Docker Desktop con Docker Compose v2
- Git y Git Bash
- Postman
- Opcional: Java 21 y Maven 3.9+ para ejecutar sin contenedores

## Ejecución

```bash
cp .env.example .env          # editar DOCKERHUB_USER y contraseñas
docker compose up --build -d  # construye las imágenes y levanta los 6 contenedores
docker compose ps             # deben aparecer los servicios en estado Up / healthy
```

La primera compilación tarda varios minutos (descarga de dependencias Maven).
`key-generator` aparece como **Exited (0)**: es normal, crea las llaves y termina.

### URLs

| Recurso | URL |
|---|---|
| API Gateway | http://localhost:8080 |
| Swagger auth-service | http://localhost:8081/swagger-ui.html |
| Swagger user-service | http://localhost:8082/swagger-ui.html |
| Health Gateway / Auth / Users | http://localhost:8080/actuator/health · :8081 · :8082 |

### Usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | Admin123! | ADMIN |
| student | Student123! | USER |

### Pruebas rápidas con curl (Git Bash)

```bash
STUDENT_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student","password":"Student123!"}' \
  | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')

curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" -H "Authorization: Bearer $STUDENT_TOKEN" \
  -d '{"firstName":"Camila","lastName":"Diaz","phone":"3001112233"}'   # 201 Created

curl -i http://localhost:8080/api/users/me -H "Authorization: Bearer $STUDENT_TOKEN"  # 200 OK
curl -i http://localhost:8080/api/users    -H "Authorization: Bearer $STUDENT_TOKEN"  # 403 Forbidden
curl -i http://localhost:8080/api/users/me                                            # 401 Unauthorized
```

### Postman

Importe los dos archivos de `postman/` y seleccione el ambiente **Taller 02 v2.0 - Local**.
Ejecute primero la carpeta *01 - Autenticación*: los scripts guardan `studentToken`, `studentId`, `adminToken` y `adminId` automáticamente.

### Verificar volúmenes, redes y bases de datos

```bash
docker volume ls          # auth-data, users-data, jwt-private, jwt-public
docker network ls         # frontend-net, backend-net
docker compose exec auth-db  psql -U postgres -d auth_db  -c "\dt"
docker compose exec users-db psql -U postgres -d users_db -c "SELECT username,first_name,last_name FROM user_profiles;"
docker compose logs --tail=50 auth-service
```

### Detener

```bash
docker compose down       # elimina contenedores, conserva los volúmenes (datos)
docker compose down -v    # elimina también los volúmenes (reinicio total)
```

## Publicación en Docker Hub

1. Crear cuenta en https://hub.docker.com
2. En `.env` definir `DOCKERHUB_USER=su_usuario`
3. Ejecutar:

```bash
bash scripts/publish-dockerhub.sh
```

El script hace `docker login`, `docker compose build`, `docker compose push` y además publica el tag `latest`.
Quedan publicadas tres imágenes: `p03-api-gateway`, `p03-auth-service`, `p03-user-service`.

**Descargar y ejecutar desde Docker Hub** (otra computadora, sin compilar): clonar el repositorio, crear `.env` con el mismo `DOCKERHUB_USER` y ejecutar:

```bash
docker compose pull auth-service user-service api-gateway
docker compose up -d --no-build
```

## Publicación en GitHub

```bash
git init
git branch -M main
git add .
git status                     # verificar que NO aparezcan .env ni archivos .pem
git commit -m "Version inicial del proyecto de microservicios con Docker"
git remote add origin https://github.com/TU-USUARIO/Proyecto-Microservicios-SpringBoot.git
git push -u origin main
```

**CI/CD:** cada `push` a `main` ejecuta GitHub Actions: compila, corre las pruebas y construye las 3 imágenes.
Si se configuran los secretos `DOCKERHUB_USERNAME` y `DOCKERHUB_TOKEN` (Settings → Secrets and variables → Actions), también las publica en Docker Hub automáticamente.

## Decisiones de seguridad

- **Database per Service:** auth-service y user-service no comparten tablas ni conexiones.
- **JWT RS256:** auth-service firma con la llave privada; user-service valida solo con la pública. La llave privada vive en un volumen que únicamente monta auth-service y nunca se sube a Git ni a la imagen.
- **BCrypt** para contraseñas; el `passwordHash` nunca se devuelve en las respuestas.
- **Stateless:** sin sesiones HTTP.
- **Mínimo privilegio:** roles ADMIN/USER, contenedores ejecutados con usuario sin privilegios (uid 1001), el gateway no tiene acceso a la red de bases de datos.
- **DTO + validación + manejo global de errores** (400, 401, 403, 404, 409).
- **Flyway** crea el esquema con migraciones versionadas; JPA solo valida (`ddl-auto: validate`).
- En producción: HTTPS, gestor de secretos, rotación de llaves, rate limiting y auditoría centralizada.

## Solución de problemas

| Problema | Acción |
|---|---|
| Puerto ocupado | Cambiar el puerto externo en `docker-compose.yml` (ej. `8090:8080`) |
| 401 Unauthorized | Verificar el header `Authorization: Bearer <token>` y que el token no haya expirado |
| 403 Forbidden | El JWT es válido pero el rol no tiene permiso |
| 409 Conflict | El usuario o el perfil ya existe |
| JWT inválido tras `down -v` | Se regeneraron las llaves: volver a hacer login |
| Reiniciar todo | `docker compose down -v` y luego `docker compose up --build -d` |

## Bibliografía

- Docker Inc. (2026). *Docker Compose file reference*. https://docs.docker.com/reference/compose-file/
- Spring (2026). *Spring Boot Reference Documentation*. https://docs.spring.io/spring-boot/
- Spring (2026). *Spring Security – OAuth2 Resource Server JWT*. https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
- Spring (2026). *Spring Cloud Gateway*. https://docs.spring.io/spring-cloud-gateway/
- Díaz Santacruz, C. A. (2026). *Proyecto 03 – Guía completa y Paso a paso para subir un proyecto a GitHub*. Universidad de Ibagué.
