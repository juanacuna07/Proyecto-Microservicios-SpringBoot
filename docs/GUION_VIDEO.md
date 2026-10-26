# Guion del video – Proyecto 03

Integrantes: Juan Camilo Acuña, Luis Miguel Hernández, Stephany Trujillo, Daniel Arteaga
Duración sugerida: 12–15 minutos. **Todos los integrantes deben aparecer y hablar** (criterio de participación).

---

## Parte 1 – Juan Camilo Acuña (≈3 min): introducción y SERVICIOS

1. Presentación del grupo y del objetivo: aplicación multicontenedor con Docker Compose.
2. Mostrar en VS Code la estructura del proyecto (api-gateway, auth-service, user-service, postman, scripts).
3. Abrir `docker-compose.yml` y explicar el encabezado y la sección **1) SERVICIOS**:
   - `auth-db` y `users-db`: PostgreSQL 17, una base por microservicio (Database per Service).
   - `key-generator`: contenedor de procesamiento que genera el par de llaves RSA y termina.
   - `auth-service`, `user-service`, `api-gateway`: se construyen con `build` (Dockerfile multietapa) y se nombran con `image` para Docker Hub.
4. Mostrar brevemente un Dockerfile: etapa Maven que compila y etapa JRE con usuario sin privilegios.

## Parte 2 – Luis Miguel Hernández (≈3 min): ENLACES y VOLÚMENES

1. **Enlaces:**
   - `depends_on` con `condition: service_healthy` (espera a que PostgreSQL responda `pg_isready`).
   - `condition: service_completed_successfully` (espera a que existan las llaves).
   - `links` y URLs por nombre de servicio: `jdbc:postgresql://auth-db:5432/auth_db`, `http://auth-service:8081`.
2. **Volúmenes** (sección 3): `auth-data`, `users-data`, `jwt-private`, `jwt-public`.
   - Explicar por qué `jwt-private` solo lo monta auth-service y user-service solo recibe la pública (`:ro`).
3. Demostración en terminal:
   - `docker compose up --build -d`
   - `docker compose ps`
   - `docker volume ls`
   - Concepto: `docker compose down` conserva los datos; `docker compose down -v` los borra.

## Parte 3 – Stephany Trujillo (≈4 min): PUERTOS, REDES y pruebas

1. **Puertos**: 8080 gateway, 8081/8082 Swagger de cada servicio, 5433/5434 PostgreSQL.
2. **Redes**: `frontend-net` y `backend-net`; el gateway no puede llegar a las bases de datos.
   - `docker network ls` y `docker network inspect proyecto03-microservicios_backend-net`
3. Navegador: Swagger en 8081 y 8082, y `/actuator/health` de los tres servicios.
4. Postman (colección incluida):
   - Login student y admin (tokens guardados en variables).
   - Crear perfil → 201, consultar /me → 200, actualizar → 200.
   - Student lista usuarios → **403**; sin token → **401**; admin lista → 200; admin elimina → 204.
5. `docker compose exec users-db psql -U postgres -d users_db -c "SELECT * FROM user_profiles;"`

## Parte 4 – Daniel Arteaga (≈3 min): publicación en DOCKER HUB y GITHUB

1. Docker Hub:
   - Mostrar `.env` con `DOCKERHUB_USER` (sin mostrar contraseñas).
   - `bash scripts/publish-dockerhub.sh` (o `docker login` + `docker compose build` + `docker compose push`).
   - Mostrar en hub.docker.com los 3 repositorios publicados.
2. GitHub:
   - `git add .`, `git status` (señalar que `.env` y `.pem` NO aparecen), `git commit`, `git remote add origin`, `git push -u origin main`.
   - Mostrar el repositorio en el navegador y la pestaña **Actions** con el pipeline CI/CD.
3. Cierre del grupo: aprendizajes y aspectos de seguridad (BCrypt, JWT RSA, mínimo privilegio, secretos fuera de Git).

---

### Consejos de grabación
- Antes de grabar, ejecutar `docker compose down -v` para mostrar el arranque limpio.
- La primera compilación tarda; dejar las imágenes construidas antes y en el video usar `docker compose up -d`.
- Exportar en MP4.
