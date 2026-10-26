#!/usr/bin/env bash
# Inicia la aplicación (necesita MySQL y el archivo .env). Se detiene con Ctrl + C.
#
#   ./scripts/start.sh
set -euo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/_comun.sh"
exigir_java
exigir_env

echo "Iniciando la aplicación (la primera vez tarda unos minutos)..."
echo "Cuando el texto deje de avanzar, abra http://localhost:8080/"
exec ./mvnw -B jetty:run "$@"
