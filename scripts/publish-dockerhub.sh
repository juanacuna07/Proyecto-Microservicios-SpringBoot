#!/usr/bin/env bash
# Construye y publica las 3 imagenes del proyecto en Docker Hub.
# Requisitos: Docker Desktop abierto y la variable DOCKERHUB_USER definida en .env
# Uso (Git Bash, desde la raiz):  bash scripts/publish-dockerhub.sh
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f .env ]; then
  echo "No existe .env. Ejecute: cp .env.example .env  y defina DOCKERHUB_USER" ; exit 1
fi
set -a; . ./.env; set +a

if [ -z "${DOCKERHUB_USER:-}" ] || [ "$DOCKERHUB_USER" = "tu_usuario_dockerhub" ]; then
  echo "Defina su usuario real de Docker Hub en .env (DOCKERHUB_USER=...)" ; exit 1
fi
TAG="${IMAGE_TAG:-2.0.0}"

echo "==> 1. Inicio de sesion en Docker Hub"
docker login

echo "==> 2. Construccion de imagenes"
docker compose build

echo "==> 3. Publicacion (tag $TAG)"
docker compose push auth-service user-service api-gateway

echo "==> 4. Publicacion del tag latest"
for svc in api-gateway auth-service user-service; do
  docker tag "$DOCKERHUB_USER/p03-$svc:$TAG" "$DOCKERHUB_USER/p03-$svc:latest"
  docker push "$DOCKERHUB_USER/p03-$svc:latest"
done

echo
echo "Imagenes publicadas:"
for svc in api-gateway auth-service user-service; do
  echo "  https://hub.docker.com/r/$DOCKERHUB_USER/p03-$svc"
done
