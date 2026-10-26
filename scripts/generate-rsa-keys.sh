#!/usr/bin/env bash
# Genera el par de llaves RSA para ejecutar los servicios SIN Docker (mvn spring-boot:run).
# Con Docker Compose NO es necesario: el servicio key-generator lo hace automaticamente.
# Uso (desde la raiz del proyecto, en Git Bash):  bash scripts/generate-rsa-keys.sh
set -euo pipefail

KEY_DIR="$(cd "$(dirname "$0")/.." && pwd)/keys"
mkdir -p "$KEY_DIR"

if [ -f "$KEY_DIR/private.pem" ]; then
  echo "Ya existen llaves en $KEY_DIR (borrelas si desea regenerarlas)."
  exit 0
fi

# Privada en PKCS#8 y publica en X.509 (formatos que lee Java)
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out "$KEY_DIR/private.pem"
openssl pkey -in "$KEY_DIR/private.pem" -pubout -out "$KEY_DIR/public.pem"
chmod 600 "$KEY_DIR/private.pem"

echo "Llaves generadas en $KEY_DIR"
echo "Recuerde: si regenera las llaves, los JWT anteriores dejan de ser validos."
